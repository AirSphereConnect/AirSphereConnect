import { Component, OnInit, AfterViewInit, OnDestroy, signal, inject, output, input, effect, computed, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import * as L from 'leaflet';
import { CityService } from '../../../../core/services/city';
import { AirQualityService } from '../../../../core/services/air-quality';
import { WeatherService } from '../../../../core/services/weather';
import { City } from '../../../../core/models/city.model';
import { AirQualityMeasurement, WeatherMeasurement, AirQualityIndex, AirQualityData } from '../../../../core/models/data.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

interface CityMapPoint extends City {
  airQualityIndex?: number;
  airQualityLabel?: string;
  airQualityColor?: string;
  airQualityAlert?: boolean;
  airQualityAlertMessage?: string;
  latestMeasurement?: AirQualityMeasurement;
  latestWeather?: WeatherMeasurement;
}

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [],
  templateUrl: './map.html',
  styleUrls: ["map.scss"]
})

export class Map implements OnInit, AfterViewInit, OnDestroy {
  private readonly cityService = inject(CityService);
  private readonly airQualityService = inject(AirQualityService);
  private readonly weatherService = inject(WeatherService);
  private readonly destroyRef = inject(DestroyRef);

  private map: L.Map | null = null;
  private readonly cityMarkers: Array<{ marker: L.CircleMarker; city: CityMapPoint; popupBound: boolean }> = [];

  mapData = signal<CityMapPoint[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);
  selectedCity = signal<CityMapPoint | null>(null);
  tooltipPosition = signal<{ x: number; y: number } | null>(null);
  isLoadingCityDetails = signal(false);
  fallbackCities = signal<AirQualityData[] | null>(null);

  // Input moderne pour recevoir le nom de la ville de l'utilisateur
  userCityName = input<string>('');

  // Computed pour trouver la ville de l'utilisateur dans les données
  userCity = computed(() => {
    const cityName = this.userCityName();
    if (!cityName) return null;
    return this.mapData().find(city =>
      city.name.toLowerCase() === cityName.toLowerCase()
    ) || null;
  });

  // Output pour notifier le parent du changement de ville
  citySelected = output<string>();

  constructor() {
    effect(() => {
      const city = this.userCity();
      if (city && this.map) {
        this.map.setView([city.latitude, city.longitude], 10);
      }
    });
  }

  ngOnInit() {
    this.loadMapData();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      if (!this.isLoading()) {
        this.initMap();
      }
    }, 100);
  }

  closeTooltip() {
    this.selectedCity.set(null);
    this.tooltipPosition.set(null);
  }

  viewCityGraphs(cityName: string) {
    this.citySelected.emit(cityName);
    this.closeTooltip();
  }

  ngOnDestroy() {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  loadMapData() {
    this.isLoading.set(true);
    this.error.set(null);

    this.cityService.getAll()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (cities) => {
          const validCities = cities.filter(c =>
            c.latitude &&
            c.longitude &&
            c.latitude >= 42 && c.latitude <= 45 &&
            c.longitude >= -1 && c.longitude <= 4.5
          );

          const cityMapPoints: CityMapPoint[] = validCities.map(city => ({
            ...city
          }));

          this.mapData.set(cityMapPoints);
          this.isLoading.set(false);

          setTimeout(() => this.initMap(), 0);
        },
        error: () => {
          this.error.set('Impossible de charger les villes');
          this.isLoading.set(false);
        }
      });
  }

  private loadCityDetails(city: CityMapPoint) {
    this.fallbackCities.set(null);
    this.selectedCity.set(city);
    this.isLoadingCityDetails.set(true);

    forkJoin({
      airQuality: this.airQualityService.getComplete(city.name).pipe(catchError(() => of(null))),
      weather: this.weatherService.getLatest(city.id).pipe(catchError(() => of(null)))
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          const aq: AirQualityIndex | undefined = data.airQuality?.latestIndex;
          const measurement = data.airQuality?.latestMeasurement;
          const weather = data.weather;

          const enrichedCity: CityMapPoint = {
            ...city,
            airQualityIndex: aq?.qualityIndex,
            airQualityLabel: aq?.qualityLabel,
            airQualityColor: aq?.qualityColor,
            airQualityAlert: aq?.alert,
            airQualityAlertMessage: aq?.alertMessage,
            latestMeasurement: measurement,
            latestWeather: weather || undefined
          };

          this.selectedCity.set(enrichedCity);
          this.isLoadingCityDetails.set(false);

          const hasNoAirQualityData = !aq && !measurement;
          if (hasNoAirQualityData && city.inseeCode && city.inseeCode.length >= 2) {
            const departmentCode = city.inseeCode.substring(0, 2);

            this.airQualityService.getTopCitiesInDepartment(departmentCode, 2)
              .pipe(
                takeUntilDestroyed(this.destroyRef),
                catchError(() => of([]))
              )
              .subscribe({
                next: (fallbackCities) => {
                  this.fallbackCities.set(fallbackCities);
                }
              });
          }
        },
        error: () => {
          this.isLoadingCityDetails.set(false);
        }
      });
  }

  private initMap() {
    if (this.map || this.mapData().length === 0) return;

    const userCity = this.userCity();
    let center: [number, number];
    let zoom: number;

    if (userCity) {
      center = [userCity.latitude, userCity.longitude];
      zoom = 10;
    } else {
      center = [43.6, 1.45];
      zoom = 7;
    }

    this.map = L.map('map').setView(center, zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      maxZoom: 19
    }).addTo(this.map);

    setTimeout(() => {
      if (this.map) {
        this.map.invalidateSize();
      }
    }, 100);

    this.addCityMarkers();
    this.map.on('zoomend', () => this.updateMarkersVisibility());
  }

  private addCityMarkers() {
    if (!this.map) return;

    for (const city of this.mapData()) {
      const color = this.getCityColor(city);
      const radius = this.getCityRadius(city);

      const marker = L.circleMarker([city.latitude, city.longitude], {
        radius: radius,
        fillColor: color,
        color: '#fff',
        weight: 2,
        opacity: 1,
        fillOpacity: 0.7,
        interactive: true
      });

      this.cityMarkers.push({ marker, city, popupBound: false });
    }

    this.updateMarkersVisibility();
  }

  private updateMarkersVisibility() {
    if (!this.map) return;

    const zoom = this.map.getZoom();
    let shownCount = 0;

    for (const item of this.cityMarkers) {
      const { marker, city } = item;
      const shouldShow = this.shouldShowCity(city, zoom);

      if (shouldShow) {
        if (!this.map!.hasLayer(marker)) {
          marker.addTo(this.map!);

          // Ajouter le click handler une seule fois
          if (!item.popupBound) {
            marker.on('click', (e: L.LeafletMouseEvent) => {
              const mapContainer = document.getElementById('map');
              if (mapContainer) {
                const rect = mapContainer.getBoundingClientRect();
                this.tooltipPosition.set({
                  x: e.originalEvent.clientX - rect.left + 10,
                  y: e.originalEvent.clientY - rect.top + 10
                });
              }

              this.loadCityDetails(city);

              if (this.map) {
                this.map.flyTo([city.latitude, city.longitude], 12, {
                  duration: 1.5,
                  easeLinearity: 0.25
                });
              }
            });

            item.popupBound = true;
          }

          shownCount++;
        }
      } else if (this.map!.hasLayer(marker)) {
        this.map!.removeLayer(marker);
      }
    }
  }

  private shouldShowCity(city: CityMapPoint, zoom: number): boolean {
    if (zoom < 8) {
      return city.population > 100000;
    }

    if (zoom < 10) {
      return city.population > 10000;
    }

    return true;
  }

  private getCityColor(city: CityMapPoint): string {
    if (city.population > 100000) return '#E91E63';
    if (city.population > 10000) return '#2196F3';
    return '#4CAF50';
  }

  private getCityRadius(city: City): number {
    const minRadius = 2;
    const maxRadius = 8;
    const minPop = 100;
    const maxPop = 500000;

    const normalized = Math.log(city.population || minPop) / Math.log(maxPop);
    return minRadius + (maxRadius - minRadius) * normalized;
  }

}
