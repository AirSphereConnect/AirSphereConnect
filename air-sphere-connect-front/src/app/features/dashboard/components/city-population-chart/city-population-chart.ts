import {Component, computed, effect, inject, input, signal} from '@angular/core';
import {
  VisXYContainerModule,
  VisGroupedBarModule,
  VisAxisModule,
  VisTooltipModule,
} from '@unovis/angular';
import {GroupedBar} from '@unovis/ts';
import {CityService} from '../../../../core/services/city';
import {City} from '../../../../core/models/city.model';

@Component({
  selector: 'app-city-population-chart',
  standalone: true,
  imports: [
    VisXYContainerModule,
    VisGroupedBarModule,
    VisAxisModule,
    VisTooltipModule,
  ],
  template: `
    <div class="bg-base-100 rounded-lg p-5 shadow-md h-full flex flex-col">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-xl font-semibold text-base-content">
          Top 10 des villes de la zone
        </h3>
        <span class="text-sm text-base-content/60">Données geo.gouv.fr</span>
      </div>

      @if (chartData().length > 0) {
        <div class="flex-1 min-h-0">
          <vis-xy-container
            [data]="chartData()"
            [margin]="{ top: 20, right: 20, bottom: 20, left: 20 }">

            <vis-grouped-bar
              [x]="x"
              [y]="y"
              [color]="color">
            </vis-grouped-bar>

            <vis-axis
              type="x"
              label="Ville"
              [tickTextAngle]="-45"
              [tickValues]="xTickValues()"
              [tickFormat]="xTickFormat">
            </vis-axis>

            <vis-axis
              type="y"
              label="Population"
              [tickFormat]="yTickFormat">
            </vis-axis>

            <vis-tooltip [triggers]="tooltipTriggers"></vis-tooltip>
          </vis-xy-container>
        </div>
      } @else {
        <p class="text-gray-400 italic text-center py-8">
          Chargement des données...
        </p>
      }
    </div>
  `,
})

export class CityPopulationChart {
  private cityService = inject(CityService);

  selectedCity = input.required<City>();
  chartData = signal<City[]>([]);

  // Génère tous les indices pour forcer l'affichage de toutes les villes
  xTickValues = computed(() => this.chartData().map((_, i) => i));

  constructor() {
    effect(() => {
      const city = this.selectedCity();
      if (city?.areaCode) {
        this.loadTopCities(city.areaCode);
      }
    });
  }

  private loadTopCities(areaCode: string) {
    this.cityService.getTopCitiesByArea(areaCode, 10).subscribe({
      next: (data) => this.chartData.set(data),
      error: (err) => console.error('Error loading top cities:', err),
    });
  }

  // ✅ Correction ici : x doit renvoyer un number
  x = (_: City, i: number) => i;
  y = (d: City) => d.population || 0;

  // ✅ Formatter pour les labels de l’axe X
  xTickFormat = (tick: number | Date, i: number): string => {
    const index = typeof tick === 'number' ? tick : i;
    const name = this.chartData()[index]?.name ?? '';
    return name.length > 6 ? name.substring(0, 5) + '.' : name;
  };

  color = () =>
    getComputedStyle(document.documentElement)
      .getPropertyValue('--color-primary')
      .trim();

  yTickFormat = (tick: number | Date): string => {
    const value = typeof tick === 'number' ? tick : 0;
    if (value >= 1_000_000) return `${(value / 1_000_000).toFixed(1)}M`;
    if (value >= 1_000) return `${Math.round(value / 1_000)}k`;
    return value.toString();
  };

  tooltipTriggers = {
    [GroupedBar.selectors.bar]: (d: City) => `
      <div style="padding: 10px; background: white; border-radius: 6px; box-shadow: 0 2px 12px rgba(0,0,0,0.2);">
        <div style="font-weight: 600; margin-bottom: 6px; font-size: 14px;">${d.name}</div>
        <div style="color: #666; font-size: 13px;">Population :
          <strong style="color: ${this.color()};">${(d.population || 0).toLocaleString('fr-FR')}</strong> habitants
        </div>
      </div>
    `,
  };
}
