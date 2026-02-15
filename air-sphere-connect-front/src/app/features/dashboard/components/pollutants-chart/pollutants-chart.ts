import {Component, input, signal, computed, effect, Input, HostListener} from '@angular/core'
import { AirQualityMeasurement } from '../../../../core/models/data.model'
import {
  VisXYContainerModule,
  VisGroupedBarModule,
  VisAxisModule,
  VisTooltipModule,
  VisBulletLegendModule,
} from '@unovis/angular'
import {NgClass} from '@angular/common';
import {GroupedBar} from '@unovis/ts';
import {User} from '../../../../core/models/user.model';

type Period = '7days' | '15days' | '30days';
type PollutantKey = 'pm25' | 'pm10' | 'no2' | 'o3' | 'so2';

@Component({
  selector: 'app-pollutants-chart',
  standalone: true,
  imports: [
    VisXYContainerModule,
    VisGroupedBarModule,
    VisAxisModule,
    VisTooltipModule,
    VisBulletLegendModule,
    NgClass,
  ],
  templateUrl: './pollutants-chart.html'
})

export class PollutantsChart {
  data = input.required<AirQualityMeasurement[]>()
  cityName = input.required<string>()

  selectedPeriod = signal<Period>('7days')
  hasAlerts = signal(false)
  alertMessage = signal('')

  private readonly screenWidth = signal(window.innerWidth);
  chartMargin = computed(() => ({
    top: 0, right: 10, bottom: 0,
    left: this.screenWidth() < 1024 ? 5 : 20
  }));

  @HostListener('window:resize')
  onResize() { this.screenWidth.set(window.innerWidth); }

  periods = [
    { value: '7days' as const, label: '7 jours' },
    { value: '15days' as const, label: '15 jours' },
    { value: '30days' as const, label: '30 jours' },
  ]

  constructor() {
    effect(() => {
      const rawData = this.data();
      this.chartData();

      this.checkAlerts(rawData);
    });
  }

  pollutants = ['pm25', 'pm10', 'no2', 'o3', 'so2']

  // Palette dynamique selon le thème DaisyUI - optimisée
  private getThemeColor(varName: string): string {
    return getComputedStyle(document.documentElement).getPropertyValue(varName).trim()
  }

  pollutantLegend = computed(() => [
    { name: 'PM2.5', color: this.getThemeColor('--color-error') },       // rouge
    { name: 'PM10',  color: this.getThemeColor('--color-secondary') },   // bleu clair
    { name: 'NO₂',   color: this.getThemeColor('--color-warning') },     // jaune
    { name: 'O₃',    color: this.getThemeColor('--color-success') },     // vert
    { name: 'SO₂',   color: this.getThemeColor('--color-info') },        // cyan
  ])

  chartData = computed(() => {
    const period = this.selectedPeriod();
    const periodToDays: Record<Period, number> = {
      '7days': 7,
      '15days': 15,
      '30days': 30
    };

    const days = periodToDays[period] ?? 7;
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - days);

    // 📊 Filtrer les données dans la période
    const filtered = this.data()
      .filter(d => new Date(d.measuredAt) >= cutoffDate);

    // 🔄 Grouper par jour (ignorer l'heure) et calculer la moyenne
    const groupedByDay = new Map<string, AirQualityMeasurement[]>();

    for (const d of filtered) {
      const date = new Date(d.measuredAt);
      // ✅ Créer une clé unique pour chaque jour (YYYY-MM-DD)
      const dayKey = date.toISOString().split('T')[0];

      if (!groupedByDay.has(dayKey)) {
        groupedByDay.set(dayKey, []);
      }
      groupedByDay.get(dayKey)!.push(d);
    }

    // 📈 Calculer la moyenne pour chaque jour
    return Array.from(groupedByDay.entries()).map(([dayKey, measurements]) => {
      const firstDate = new Date(measurements[0].measuredAt);

      // Fonction helper pour calculer la moyenne d'un polluant
      const average = (key: PollutantKey): number => {
        const values = measurements
          .map(m => m[key])
          .filter((v): v is number => v != null && !Number.isNaN(v));

        return values.length > 0
          ? values.reduce((sum, v) => sum + v, 0) / values.length
          : 0;
      };

      return {
        date: new Date(firstDate.getFullYear(), firstDate.getMonth(), firstDate.getDate()),
        pm25: average('pm25'),
        pm10: average('pm10'),
        no2: average('no2'),
        o3: average('o3'),
        so2: average('so2'),
      };
    }).sort((a, b) => a.date.getTime() - b.date.getTime());
  })

  x = (d: any) => d.date
  y = this.pollutants.map(p => (d: any) => d[p])

  colors = (d: any, i: number) => this.pollutantLegend()[i]?.color || this.getThemeColor('--color-neutral')

  // ✅ Générer tous les ticks pour l'axe X (toutes les dates en timestamp)
  xTickValues = computed(() => this.chartData().map(d => d.date.getTime()))

  dateFormat = (timestamp: number | Date) => {
    const date = timestamp instanceof Date ? timestamp : new Date(timestamp)
    return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`
  }

  // ✅ Format axe Y avec unité
  yTickFormat = (tick: number | Date): string => {
    const value = typeof tick === 'number' ? tick : 0;
    return `${Math.round(value)}`;
  }

  // ✅ Tooltip détaillé avec toutes les valeurs
  tooltipTriggers = {
    [GroupedBar.selectors.bar]: (d: any) => {
      const date = d.date instanceof Date ? d.date : new Date(d.date);
      const dateStr = `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}`;

      return `
        <div class="bg-base-100 rounded-lg p-3 shadow-lg border border-base-300" style="min-width: 180px;">
          <div class="font-semibold mb-2 text-sm border-b border-base-300 pb-1 text-base-content">
            ${dateStr}
          </div>
          ${d.pm25 ? `<div class="my-1 text-xs text-base-content"><span style="color: ${this.pollutantLegend()[0].color};">●</span> PM2.5 : <strong>${d.pm25.toFixed(1)} µg/m³</strong></div>` : ''}
          ${d.pm10 ? `<div class="my-1 text-xs text-base-content"><span style="color: ${this.pollutantLegend()[1].color};">●</span> PM10 : <strong>${d.pm10.toFixed(1)} µg/m³</strong></div>` : ''}
          ${d.no2 ? `<div class="my-1 text-xs text-base-content"><span style="color: ${this.pollutantLegend()[2].color};">●</span> NO₂ : <strong>${d.no2.toFixed(1)} µg/m³</strong></div>` : ''}
          ${d.o3 ? `<div class="my-1 text-xs text-base-content"><span style="color: ${this.pollutantLegend()[3].color};">●</span> O₃ : <strong>${d.o3.toFixed(1)} µg/m³</strong></div>` : ''}
          ${d.so2 ? `<div class="my-1 text-xs text-base-content"><span style="color: ${this.pollutantLegend()[4].color};">●</span> SO₂ : <strong>${d.so2.toFixed(1)} µg/m³</strong></div>` : ''}
        </div>
      `;
    }
  };

  // Math pour le template
  Math = Math;
  @Input() user!: User | null;

  selectPeriod(period: Period) {
    this.selectedPeriod.set(period)
  }

  checkAlerts(data: AirQualityMeasurement[]) {
    if (data.length === 0) return
    const last = data.at(-1)
    if (!last) return
    const alerts: string[] = []

    if (last.pm25 > 35) alerts.push('PM2.5')
    if (last.pm10 > 80) alerts.push('PM10')
    if (last.no2 > 100) alerts.push('NO₂')
    if (last.o3 > 120) alerts.push('O₃')
    if (last.so2 > 80) alerts.push('SO₂')

    if (alerts.length > 0) {
      this.hasAlerts.set(true)
      this.alertMessage.set(`Seuils dépassés pour : ${alerts.join(', ')}`)
    } else {
      this.hasAlerts.set(false)
      this.alertMessage.set('')
    }
  }
}
