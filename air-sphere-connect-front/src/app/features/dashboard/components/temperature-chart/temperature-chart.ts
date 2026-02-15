import { Component, input, computed, signal, HostListener } from '@angular/core';
import {
  VisXYContainerModule,
  VisLineModule,
  VisAxisModule,
  VisTooltipModule,
  VisCrosshairModule,
} from '@unovis/angular';
import { CurveType } from '@unovis/ts';
import { WeatherMeasurement } from '../../../../core/models/data.model';

@Component({
  selector: 'app-temperature-chart',
  standalone: true,
  imports: [VisXYContainerModule, VisLineModule, VisAxisModule, VisTooltipModule, VisCrosshairModule],
  template: `
    <div class="bg-base-100 rounded-lg p-5 shadow-md">
      <h3 class="text-xl text-center xl:text-start font-semibold text-base-content mb-4">


        Évolution de la température de {{ cityName() }}
      </h3>

      @if (chartData().length > 0) {
        <span class="text-xs text-base-content/50 mb-1 block">Température (°C)</span>
        <div class="overflow-x-auto">
          <div class="min-w-[500px]">
            <vis-xy-container
              [data]="chartData()"
              [height]="height()"
              [yDomain]="yDomain()"
              [margin]="chartMargin()">

              <vis-line
                [x]="x"
                [y]="y"
                [color]="color"
                [lineWidth]="3"
                [curveType]="curveType">
              </vis-line>

              <vis-axis
                type="x"
                label="Date"
                [tickFormat]="dateFormat"
                [tickTextAngle]="45"
                [tickValues]="xTickValues()"
              ></vis-axis>
              <vis-axis type="y" [tickFormat]="yTickFormat"></vis-axis>
              <vis-tooltip></vis-tooltip>
              <vis-crosshair [template]="crosshairTemplate" [color]="color"></vis-crosshair>
            </vis-xy-container>
          </div>
        </div>
      } @else {
        <p class="text-base-content opacity-60 italic text-center py-8">Aucune donnée de température disponible</p>
      }
    </div>
  `,
})

export class TemperatureChart {
  // === Inputs ===
  data = input.required<WeatherMeasurement[]>();
  cityName = input.required<string>();
  height = input<number>(300);

  private readonly screenWidth = signal(window.innerWidth);
  chartMargin = computed(() => ({
    top: 20, right: 10, bottom: 20,
    left: this.screenWidth() < 1024 ? 5 : 20
  }));

  @HostListener('window:resize')
  onResize() { this.screenWidth.set(window.innerWidth); }

  // === Données formatées pour Unovis ===
  chartData = computed(() => {
    const grouped: Record<string, number[]> = {};

    // Grouper les températures par jour
    for (const measurement of this.data()) {
      const day = new Date(measurement.measuredAt).toISOString().split('T')[0];
      if (!grouped[day]) grouped[day] = [];
      grouped[day].push(measurement.temperature);
    }

    // Moyenne par jour
    const dailyAvg = Object.entries(grouped).map(([day, temps]) => ({
      x: new Date(day),
      y: temps.reduce((a, b) => a + b, 0) / temps.length,
    }));

    // Tri
    return dailyAvg.sort((a, b) => a.x.getTime() - b.x.getTime());
  });

  // ✅ Générer des ticks réguliers avec premier et dernier jour
  xTickValues = computed(() => {
    const data = this.chartData();
    if (data.length === 0) return [];

    const maxTicks = 10; // Nombre max de ticks à afficher

    // Si on a moins de points que maxTicks, retourner tous les points
    if (data.length <= maxTicks) {
      return data.map(d => d.x.getTime());
    }

    // Sinon, prendre exactement maxTicks points répartis uniformément
    const indices = Array.from({ length: maxTicks }, (_, i) =>
      Math.round(i * (data.length - 1) / (maxTicks - 1))
    );

    return indices.map(i => data[i].x.getTime());
  });

  // ✅ Calculer le domaine Y avec marge pour éviter que la courbe touche les bords
  yDomain = computed((): [number, number] => {
    const data = this.chartData();
    if (data.length === 0) return [0, 30];

    const temperatures = data.map(d => d.y);
    const minTemp = Math.min(...temperatures);
    const maxTemp = Math.max(...temperatures);

    // Ajouter une marge de 10% de chaque côté
    const range = maxTemp - minTemp;
    const margin = Math.max(2, range * 0.1); // Minimum 2°C de marge

    return [
      Math.floor(minTemp - margin),
      Math.ceil(maxTemp + margin)
    ];
  });

  x = (d: any) => d.x;
  y = (d: any) => d.y;

  curveType = CurveType.MonotoneX;
  color = () => getComputedStyle(document.documentElement)
    .getPropertyValue('--color-primary')
    .trim();

  dateFormat = (tick: number | Date): string => {
    const date = tick instanceof Date ? tick : new Date(tick);
    return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1)
      .toString()
      .padStart(2, '0')}`;
  };

  yTickFormat = (tick: number | Date): string => {
    const value = typeof tick === 'number' ? tick : 0;
    return `${value.toFixed(1)}°C`;
  };

  crosshairTemplate = (d: any) => {
    const date = d.x instanceof Date ? d.x : new Date(d.x);
    const dateStr = `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;

    return `
      <div class="bg-base-100 rounded-lg p-3 shadow-lg border border-base-300">
        <div class="font-semibold mb-2 text-sm border-b border-base-300 pb-1 text-base-content">${dateStr}</div>
        <div class="text-sm mt-2 text-base-content">
          Température : <strong style="color: ${this.color()};">${d.y.toFixed(1)}°C</strong>
        </div>
      </div>
    `;
  };

  // Math pour le template
  Math = Math;
}
