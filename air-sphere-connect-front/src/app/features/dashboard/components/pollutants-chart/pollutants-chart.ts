import {Component, input, signal, computed, effect, Input} from '@angular/core'
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
  template: `
    <div class="bg-base-100 rounded-lg p-5 shadow-md transition-colors">
      <div class="flex justify-between items-center mb-5">
        <h3 class="text-xl font-semibold text-base-content m-0">
          @if (user) {
            Taux de polluants – {{ user.address.city.name }}
          }

        </h3>

        <div class="flex gap-1">
          @for (period of periods; track period.value) {
            <button
              class="btn btn-sm rounded-md transition-all"
              [ngClass]="selectedPeriod() === period.value
                ? 'btn-primary text-primary-content'
                : 'btn-outline btn-primary'"
              (click)="selectPeriod(period.value)">
              {{ period.label }}
            </button>
          }
        </div>
      </div>

      @if (chartData().length > 0) {
        @if (hasAlerts()) {
          <div class="bg-warning/20 border border-warning text-warning px-3 py-3 rounded mb-5 font-medium flex items-center gap-2">
            ⚠️ Alerte : {{ alertMessage() }}
          </div>
        }

        <vis-bullet-legend [items]="pollutantLegend()"></vis-bullet-legend>

        <vis-xy-container [data]="chartData()" [height]="400" [margin]="{ top: 20, right: 20, bottom: 20, left: 20 }">
          <vis-grouped-bar
            [x]="x"
            [y]="y"
            [color]="colors"
            [barPadding]="0.2"
            [groupPadding]="0.1">
          </vis-grouped-bar>

          <vis-axis
            type="x"
            label="Date"
            [tickFormat]="dateFormat"
            [tickTextAngle]="-45"
            [tickValues]="xTickValues()"></vis-axis>
          <vis-axis
            type="y"
            label="Concentration (µg/m³)"
            [tickFormat]="yTickFormat"></vis-axis>
          <vis-tooltip [triggers]="tooltipTriggers"></vis-tooltip>
        </vis-xy-container>
      } @else {
        <p class="text-base-content opacity-60 italic text-center py-8">
          Aucune donnée de pollution disponible
        </p>
      }
    </div>
  `
})
export class PollutantsChart {
  data = input.required<AirQualityMeasurement[]>()
  cityName = input.required<string>()

  selectedPeriod = signal<'7days' | '15days' | '30days'>('7days')
  hasAlerts = signal(false)
  alertMessage = signal('')

  periods = [
    { value: '7days' as const, label: '7 jours' },
    { value: '15days' as const, label: '15 jours' },
    { value: '30days' as const, label: '30 jours' },
  ]

  constructor() {
    // Debug: afficher les données reçues
    effect(() => {
      const rawData = this.data();
      const aggregated = this.chartData();

      console.log('🔍 PollutantsChart - données brutes:', rawData.length, 'mesures');
      console.log('📊 PollutantsChart - après agrégation:', aggregated.length, 'jours');

      if (aggregated.length > 0) {
        console.log('📈 Exemple jour 1:', {
          date: aggregated[0].date,
          pm25: aggregated[0].pm25?.toFixed(1),
          pm10: aggregated[0].pm10?.toFixed(1),
          no2: aggregated[0].no2?.toFixed(1),
          o3: aggregated[0].o3?.toFixed(1),
          so2: aggregated[0].so2?.toFixed(1)
        });
      }

      this.checkAlerts(rawData);
    });
  }

  pollutants = ['pm25', 'pm10', 'no2', 'o3', 'so2']

  // ✅ Palette dynamique selon le thème DaisyUI - optimisée
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
    const days = period === '7days' ? 7 : period === '15days' ? 15 : 30;
    const cutoffDate = new Date();
    cutoffDate.setDate(cutoffDate.getDate() - days);

    // 📊 Filtrer les données dans la période
    const filtered = this.data()
      .filter(d => new Date(d.measuredAt) >= cutoffDate);

    // 🔄 Grouper par jour (ignorer l'heure) et calculer la moyenne
    const groupedByDay = new Map<string, AirQualityMeasurement[]>();

    filtered.forEach(d => {
      const date = new Date(d.measuredAt);
      // ✅ Créer une clé unique pour chaque jour (YYYY-MM-DD)
      const dayKey = date.toISOString().split('T')[0];

      if (!groupedByDay.has(dayKey)) {
        groupedByDay.set(dayKey, []);
      }
      groupedByDay.get(dayKey)!.push(d);
    });

    // 📈 Calculer la moyenne pour chaque jour
    return Array.from(groupedByDay.entries()).map(([dayKey, measurements]) => {
      const firstDate = new Date(measurements[0].measuredAt);

      // Fonction helper pour calculer la moyenne d'un polluant
      const average = (key: 'pm25' | 'pm10' | 'no2' | 'o3' | 'so2'): number => {
        const values = measurements
          .map(m => m[key])
          .filter((v): v is number => v != null && !isNaN(v));

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

  selectPeriod(period: '7days' | '15days' | '30days') {
    this.selectedPeriod.set(period)
  }

  checkAlerts(data: AirQualityMeasurement[]) {
    if (data.length === 0) return
    const last = data[data.length - 1]
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
