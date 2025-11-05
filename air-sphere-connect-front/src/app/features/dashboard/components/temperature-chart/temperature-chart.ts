import { Component, input, computed } from '@angular/core';
import {
  VisXYContainerModule,
  VisLineModule,
  VisAxisModule,
  VisTooltipModule,
  VisCrosshairModule,
} from '@unovis/angular';
import { CurveType, Line } from '@unovis/ts';
import { WeatherMeasurement } from '../../../../core/models/data.model';

@Component({
  selector: 'app-temperature-chart',
  standalone: true,
  imports: [VisXYContainerModule, VisLineModule, VisAxisModule, VisTooltipModule, VisCrosshairModule],
  template: `
    <div class="bg-base-100 rounded-lg p-5 shadow-md">
      <h3 class="text-xl font-semibold text-base-content mb-4">
        Évolution de la température - {{ cityName() }}
      </h3>

      @if (chartData().length > 0) {
        <vis-xy-container
          [data]="chartData()"
          [height]="height()"
          [yDomain]="yDomain()"
          [margin]="{ top: 10, right: 20, bottom: 60, left: 60 }">

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
          <vis-axis type="y" label="Température (°C)" [tickFormat]="yTickFormat"></vis-axis>
          <vis-tooltip></vis-tooltip>
          <vis-crosshair [template]="crosshairTemplate" [color]="color"></vis-crosshair>
        </vis-xy-container>
      } @else {
        <p class="text-gray-400 italic text-center py-8">Aucune donnée de température disponible</p>
      }
    </div>
  `,
})
export class TemperatureChart {
  // === Inputs ===
  data = input.required<WeatherMeasurement[]>();
  cityName = input.required<string>();
  height = input<number>(300);

  // === Données formatées pour Unovis ===
  chartData = computed(() => {
    const grouped: Record<string, number[]> = {};

    // Grouper les températures par jour
    this.data().forEach(d => {
      const day = new Date(d.measuredAt).toISOString().split('T')[0];
      if (!grouped[day]) grouped[day] = [];
      grouped[day].push(d.temperature);
    });

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
    const timestamps: number[] = [];

    // Toujours inclure le premier
    timestamps.push(data[0].x.getTime());

    // Si on a plus de données que maxTicks, prendre des points réguliers
    if (data.length > maxTicks) {
      const step = Math.floor((data.length - 1) / (maxTicks - 1));
      for (let i = step; i < data.length - 1; i += step) {
        timestamps.push(data[i].x.getTime());
      }
    } else {
      // Sinon, prendre tous les points intermédiaires
      for (let i = 1; i < data.length - 1; i++) {
        timestamps.push(data[i].x.getTime());
      }
    }

    // Toujours inclure le dernier (si différent du premier)
    if (data.length > 1) {
      timestamps.push(data[data.length - 1].x.getTime());
    }

    return timestamps;
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
      <div style="padding: 10px; background: white; border-radius: 6px; box-shadow: 0 2px 12px rgba(0,0,0,0.2);">
        <div style="font-weight: 600; margin-bottom: 6px; font-size: 13px; border-bottom: 1px solid #e0e0e0; padding-bottom: 4px;">${dateStr}</div>
        <div style="font-size: 14px; margin-top: 6px;">
          Température : <strong style="color: ${this.color()};">${d.y.toFixed(1)}°C</strong>
        </div>
      </div>
    `;
  };

  // Math pour le template
  Math = Math;
}
