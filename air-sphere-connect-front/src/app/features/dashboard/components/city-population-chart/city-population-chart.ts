import { Component, computed, effect, inject, input, signal } from '@angular/core';
import {
  VisXYContainerModule,
  VisGroupedBarModule,
  VisAxisModule,
  VisTooltipModule,
} from '@unovis/angular';
import { GroupedBar } from '@unovis/ts';
import { CityService } from '../../../../core/services/city';
import { City } from '../../../../core/models/city.model';

@Component({
  selector: 'app-city-population-chart',
  standalone: true,
  imports: [
    VisXYContainerModule,
    VisGroupedBarModule,
    VisAxisModule,
    VisTooltipModule,
  ],
  templateUrl: './city-population-chart.html',
})
export class CityPopulationChart {
  private readonly cityService = inject(CityService);

  selectedCity = input.required<City>();
  chartData = signal<City[]>([]);

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
      error: (err) => this.logError('Error loading top cities', err),
    });
  }

  private logError(message: string, error?: unknown): void {
    if (typeof ngDevMode !== 'undefined' && ngDevMode) {
      console.error(message, error);
    }
  }

  x = (_: City, i: number) => i;
  y = (d: City) => d.population || 0;

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
      <div class="bg-base-100 rounded-lg p-3 shadow-lg border border-base-300">
        <div class="font-semibold mb-2 text-sm text-base-content">${d.name}</div>
        <div class="text-sm text-base-content opacity-70">
          Population :
          <strong style="color: ${this.color()};">
            ${(d.population || 0).toLocaleString('fr-FR')}
          </strong> habitants
        </div>
      </div>
    `,
  };
}
