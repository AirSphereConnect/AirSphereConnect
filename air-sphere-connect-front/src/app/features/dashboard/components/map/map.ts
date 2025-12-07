import { Component, OnInit, AfterViewInit, OnDestroy, signal, inject, output, input, effect, computed, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import { CityService } from '../../../../core/services/city';
import { AirQualityService } from '../../../../core/services/air-quality';
import { WeatherService } from '../../../../core/services/weather';
import { UserService } from '../../../../shared/services/user-service';
import { FavoritesService } from '../../../../shared/services/favorites-service';
import { NotificationService } from '../../../../shared/services/notification-service';
import { City } from '../../../../core/models/city.model';
import { AirQualityMeasurement, WeatherMeasurement, AirQualityIndex, AirQualityData } from '../../../../core/models/data.model';
import { IconComponent } from '../../../../shared/components/ui/icon/icon';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {Button} from '../../../../shared/components/ui/button/button';

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
  imports: [IconComponent, Button],
  templateUrl: './map.html',
  styleUrls: ["map.scss"]
})

export class Map implements OnInit, AfterViewInit, OnDestroy {
  private readonly cityService = inject(CityService);
  private readonly airQualityService = inject(AirQualityService);
  private readonly weatherService = inject(WeatherService);
  private readonly userService = inject(UserService);
  private readonly favoritesService = inject(FavoritesService);
  private readonly notificationService = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  private map: L.Map | null = null;
  private readonly cityMarkers: Array<{ marker: L.CircleMarker; city: CityMapPoint; popupBound: boolean }> = [];

  mapData = signal<CityMapPoint[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);
  selectedCity = signal<CityMapPoint | null>(null);
  tooltipPosition = signal<{ x: number; y: number } | null>(null);
  isLoadingCityDetails = signal(false);
  fallbackCities = signal<AirQualityData[] | null>(null);
  favoriteCityNames = signal<string[]>([]);

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
    this.loadUserFavorites();
  }

  private loadUserFavorites() {
    this.userService.userProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(profile => {
        if (profile?.user?.favorites) {
          const favoriteNames = profile.user.favorites.map(fav => fav.cityName);
          this.favoriteCityNames.set(favoriteNames);
          // Mettre à jour les marqueurs si la carte est déjà initialisée
          if (this.map) {
            this.updateMarkersStyle();
          }
        }
      });
  }

  isCityFavorite(cityName: string): boolean {
    return this.favoriteCityNames().includes(cityName);
  }

  private updateMarkersStyle() {
    for (const item of this.cityMarkers) {
      const { marker, city } = item;
      const isFavorite = this.isCityFavorite(city.name);

      marker.setStyle({
        color: isFavorite ? '#FFD700' : '#fff',
        weight: isFavorite ? 3 : 2,
        fillOpacity: isFavorite ? 0.85 : 0.7
      });
    }
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

  addToFavorites() {
    const city = this.selectedCity();
    if (!city) return;

    const payload = {
      selectWeather: true,
      selectAirQuality: true,
      selectPopulation: true,
      cityId: city.id
    };

    this.favoritesService.addFavorites(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          const currentFavorites = this.favoriteCityNames();
          this.favoriteCityNames.set([...currentFavorites, city.name]);
          this.updateMarkersStyle();
          this.notificationService.showSuccess(`${city.name} ajouté aux favoris`);
          this.userService.fetchUserProfile();
          this.closeTooltip();
        },
        error: (err) => {
          console.error('Erreur lors de l\'ajout du favori', err);
          this.notificationService.showError('Erreur lors de l\'ajout du favori');
        }
      });
  }

  removeFromFavorites() {
    const city = this.selectedCity();
    if (!city) return;

    this.userService.userProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(profile => {
        const favorite = profile?.user?.favorites?.find(fav => fav.cityName === city.name);
        if (favorite) {
          this.favoritesService.deleteFavorites(favorite.id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
              next: () => {
                const currentFavorites = this.favoriteCityNames();
                this.favoriteCityNames.set(currentFavorites.filter(name => name !== city.name));
                this.updateMarkersStyle();
                this.notificationService.showSuccess(`${city.name} retiré des favoris`);
                this.userService.fetchUserProfile();
                this.closeTooltip();
              },
              error: (err) => {
                console.error('Erreur lors de la suppression du favori', err);
                this.notificationService.showError('Erreur lors de la suppression');
              }
            });
        }
      });
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
      const isFavorite = this.isCityFavorite(city.name);

      const marker = L.circleMarker([city.latitude, city.longitude], {
        radius: radius,
        fillColor: color,
        color: isFavorite ? '#FFD700' : '#fff',
        weight: isFavorite ? 3 : 2,
        opacity: 1,
        fillOpacity: isFavorite ? 0.85 : 0.7,
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
        if (!this.map.hasLayer(marker)) {
          marker.addTo(this.map);

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
      } else if (this.map.hasLayer(marker)) {
        this.map.removeLayer(marker);
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
