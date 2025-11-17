import { Component, OnInit, AfterViewInit, OnDestroy, signal, inject, output, input, effect, computed, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import * as L from 'leaflet';
import { CityService } from '../../../../core/services/city';
import { AirQualityService } from '../../../../core/services/air-quality';
import { WeatherService } from '../../../../core/services/weather';
import { City } from '../../../../core/models/city.model';
import { AirQualityMeasurement, WeatherMeasurement, AirQualityIndex } from '../../../../core/models/data.model';
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
  template: `
    <div class="relative bg-base-100 rounded-lg p-5 shadow-md">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-xl font-semibold text-base-content m-0">
          🗺️ Carte des villes d'Occitanie
        </h3>

        @if (isLoading()) {
          <span class="bg-amber-400 text-white px-3 py-1 rounded-full text-xs font-semibold">
        Chargement...
      </span>
        }
        @if (!isLoading()) {
          <span class="bg-indigo-500 text-white px-3 py-1 rounded-full text-xs font-semibold">
        {{ mapData().length }} villes
      </span>
        }
      </div>

      <div id="map"
           class="w-full h-[500px] rounded-lg overflow-hidden shadow-inner"></div>

      <!-- Tooltip personnalisé -->
      @if (selectedCity()) {
        <div class="absolute bg-base-100 rounded-lg shadow-2xl border border-base-300 p-5 z-[2000]"
             [style.left.px]="tooltipPosition()?.x"
             [style.top.px]="tooltipPosition()?.y"
             style="max-width: 350px; min-width: 300px;">
          <div class="flex justify-between items-start mb-3">
            <h3 class="text-lg font-bold text-base-content m-0">
              {{ selectedCity()!.name }}
            </h3>
            <button (click)="closeTooltip()"
                    class="text-base-content opacity-60 hover:opacity-100 text-xl leading-none">
              ×
            </button>
          </div>

          <div class="space-y-2 text-sm">
            <div class="flex items-center">
              <span class="text-base-content opacity-70 font-medium w-32">📍 Département :</span>
              <span class="text-base-content">{{ selectedCity()!.departmentName || 'Non mesuré' }} ({{ selectedCity()!.postalCode || 'Non mesuré' }})</span>
            </div>

            <div class="flex items-center">
              <span class="text-base-content opacity-70 font-medium w-32">👥 Population :</span>
              <span class="text-base-content font-semibold">{{ selectedCity()!.population.toLocaleString('fr-FR') }} hab.</span>
            </div>

            <!-- Loading indicator -->
            @if (isLoadingCityDetails()) {
              <div class="mt-3 p-3 bg-base-200 rounded-lg">
                <div class="flex items-center gap-2">
                  <span class="loading loading-spinner loading-sm"></span>
                  <span class="text-sm">Chargement des données...</span>
                </div>
              </div>
            }

            <!-- Indice de qualité de l'air -->
            @if (selectedCity()!.airQualityLabel) {
              <div class="mt-3 p-3 bg-base-200 rounded-lg">
                <div class="flex items-center justify-between mb-2">
                  <span class="text-base-content font-semibold text-sm">🏭 Qualité de l'air</span>
                  <span class="inline-block px-3 py-1 rounded-full text-white text-xs font-semibold"
                        [style.background-color]="selectedCity()!.airQualityColor">
                    {{ selectedCity()!.airQualityLabel }} ({{ selectedCity()!.airQualityIndex }})
                  </span>
                </div>

                @if (selectedCity()!.airQualityAlert) {
                  <div class="mt-2 p-2 bg-orange-100 border border-orange-300 rounded text-xs text-orange-800">
                    ⚠️ {{ selectedCity()!.airQualityAlertMessage || 'Alerte pollution' }}
                  </div>
                }
              </div>
            }

            <!-- Polluants du jour -->
            @if (selectedCity()!.latestMeasurement) {
              <div class="mt-3 p-3 bg-base-200 rounded-lg">
                <h4 class="text-base-content font-semibold text-sm mb-2">🧪 Polluants ({{ selectedCity()!.latestMeasurement!.unit }})</h4>
                <div class="grid grid-cols-3 gap-2 text-xs">
                  <div class="flex flex-col">
                    <span class="text-base-content opacity-70">PM2.5</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestMeasurement!.pm25 != null ? selectedCity()!.latestMeasurement!.pm25.toFixed(1) : 'Non mesuré' }}</span>
                  </div>
                  <div class="flex flex-col">
                    <span class="text-base-content opacity-70">PM10</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestMeasurement!.pm10 != null ? selectedCity()!.latestMeasurement!.pm10.toFixed(1) : 'Non mesuré' }}</span>
                  </div>
                  <div class="flex flex-col">
                    <span class="text-base-content opacity-70">NO₂</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestMeasurement!.no2 != null ? selectedCity()!.latestMeasurement!.no2.toFixed(1) : 'Non mesuré' }}</span>
                  </div>
                  <div class="flex flex-col">
                    <span class="text-base-content opacity-70">O₃</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestMeasurement!.o3 != null ? selectedCity()!.latestMeasurement!.o3.toFixed(1) : 'Non mesuré' }}</span>
                  </div>
                  <div class="flex flex-col">
                    <span class="text-base-content opacity-70">SO₂</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestMeasurement!.so2 != null ? selectedCity()!.latestMeasurement!.so2.toFixed(1) : 'Non mesuré' }}</span>
                  </div>
                  <div class="flex flex-col">
                    <span class="text-base-content opacity-70">CO</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestMeasurement!.co != null ? selectedCity()!.latestMeasurement!.co.toFixed(1) : 'Non mesuré' }}</span>
                  </div>


                </div>
              </div>
            }

            <!-- Météo du jour -->
            @if (selectedCity()!.latestWeather) {
              <div class="mt-3 p-3 bg-base-200 rounded-lg">
                <h4 class="text-base-content font-semibold text-sm mb-2">🌤️ Météo</h4>
                <div class="grid grid-cols-2 gap-2 text-xs">
                  <div class="flex items-center">
                    <span class="text-base-content opacity-70 mr-1">🌡️</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestWeather!.temperature != null ? selectedCity()!.latestWeather!.temperature.toFixed(1) + '°C' : 'Non mesuré' }}</span>
                  </div>
                  <div class="flex items-center">
                    <span class="text-base-content opacity-70 mr-1">💧</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestWeather!.humidity != null ? selectedCity()!.latestWeather!.humidity.toFixed(0) + '%' : 'Non mesuré' }}</span>
                  </div>
                  <div class="flex items-center">
                    <span class="text-base-content opacity-70 mr-1">🌬️</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestWeather!.windSpeed != null ? selectedCity()!.latestWeather!.windSpeed.toFixed(1) + ' km/h' : 'Non mesuré' }}</span>
                  </div>
                  <div class="flex items-center">
                    <span class="text-base-content opacity-70 mr-1">🔽</span>
                    <span class="font-bold text-base-content">{{ selectedCity()!.latestWeather!.pressure != null ? selectedCity()!.latestWeather!.pressure.toFixed(0) + ' hPa' : 'Non mesuré' }}</span>
                  </div>
                </div>
              </div>
            }
          </div>

          <button (click)="viewCityGraphs(selectedCity()!.name)"
                  class="mt-4 w-full py-2.5 px-4 bg-secondary text-white font-semibold rounded-lg hover:opacity-90 transition-opacity">
            📊 Voir les graphiques
          </button>
        </div>
      }

      <div
        class="absolute bottom-10 right-10 bg-base-100 p-4 rounded-lg shadow-lg border border-base-300 z-[1000]">
        <h4 class="m-0 mb-2 text-sm font-semibold text-base-content">Légende</h4>

        <div class="flex items-center mb-2 text-xs text-base-content opacity-70">
      <span
        class="w-4 h-4 rounded-full mr-2 border-2 border-base-300 shadow-sm"
        style="background-color: #4CAF50"></span>
          <span>Petite ville (&lt; 10k hab.)</span>
        </div>

        <div class="flex items-center mb-2 text-xs text-base-content opacity-70">
      <span
        class="w-4 h-4 rounded-full mr-2 border-2 border-base-300 shadow-sm"
        style="background-color: #2196F3"></span>
          <span>Ville moyenne (10k-100k hab.)</span>
        </div>

        <div class="flex items-center text-xs text-base-content opacity-70">
      <span
        class="w-4 h-4 rounded-full mr-2 border-2 border-base-300 shadow-sm"
        style="background-color: #E91E63"></span>
          <span>Grande ville (&gt; 100k hab.)</span>
        </div>
      </div>

      @if (error()) {
        <div class="mt-4 p-4 bg-red-50 border border-red-200 rounded-md text-red-700 text-sm">
          ⚠️ {{ error() }}
        </div>
      }
    </div>

`,
  styleUrls: ["map.scss"]
})

export class Map implements OnInit, AfterViewInit, OnDestroy {
  private cityService = inject(CityService);
  private airQualityService = inject(AirQualityService);
  private weatherService = inject(WeatherService);
  private destroyRef = inject(DestroyRef);

  private map: L.Map | null = null;
  private cityMarkers: Array<{ marker: L.CircleMarker; city: CityMapPoint; popupBound: boolean }> = [];

  mapData = signal<CityMapPoint[]>([]);
  isLoading = signal(true);
  error = signal<string | null>(null);
  selectedCity = signal<CityMapPoint | null>(null);
  tooltipPosition = signal<{ x: number; y: number } | null>(null);
  isLoadingCityDetails = signal(false);

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
    // Effect pour recentrer la carte quand la ville de l'utilisateur est trouvée
    effect(() => {
      const city = this.userCity();
      if (city && this.map) {
        console.log('🎯 Centrage de la carte sur la ville de l\'utilisateur:', city.name);
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
    console.log('📊 Affichage des graphiques pour:', cityName);
    this.citySelected.emit(cityName);
    this.closeTooltip();
  }

  ngOnDestroy() {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  /**
   * 📍 Charger toutes les villes d'Occitanie (sans données air/météo)
   */
  loadMapData() {
    this.isLoading.set(true);
    this.error.set(null);

    this.cityService.getAll()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (cities) => {
          console.log(`✅ ${cities.length} villes chargées`);

          // Filtrer les villes avec coordonnées valides
          const validCities = cities.filter(c =>
            c.latitude &&
            c.longitude &&
            c.latitude >= 42 && c.latitude <= 45 && // Occitanie
            c.longitude >= -1 && c.longitude <= 4.5
          );

          console.log(`✅ ${validCities.length} villes avec coordonnées valides`);

          // Convertir en CityMapPoint sans charger les données (lazy loading)
          const cityMapPoints: CityMapPoint[] = validCities.map(city => ({
            ...city
          }));

          this.mapData.set(cityMapPoints);
          this.isLoading.set(false);

          setTimeout(() => this.initMap(), 0);
        },
        error: (err) => {
          console.error('❌ Erreur chargement villes:', err);
          this.error.set('Impossible de charger les villes');
          this.isLoading.set(false);
        }
      });
  }

  /**
   * 🔍 Charger les données d'une ville spécifique au clic (lazy loading)
   */
  private loadCityDetails(city: CityMapPoint) {
    console.log(`🔄 Chargement lazy des données pour ${city.name}...`);

    // Afficher immédiatement le tooltip avec les infos de base + loading
    this.selectedCity.set(city);
    this.isLoadingCityDetails.set(true);

    forkJoin({
      airQuality: this.airQualityService.getComplete(city.name).pipe(catchError(() => of(null))),
      weather: this.weatherService.getLatest(city.id).pipe(catchError(() => of(null)))
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          const aq = data.airQuality?.latestIndex;
          const measurement = data.airQuality?.latestMeasurement;
          const weather = data.weather;

          console.log(`✅ Données chargées pour ${city.name}:`, { aq, measurement, weather });

          // Mettre à jour la ville avec les données enrichies
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

          // Mettre à jour le tooltip avec les données enrichies
          this.selectedCity.set(enrichedCity);
          this.isLoadingCityDetails.set(false);
        },
        error: (err) => {
          console.error(`❌ Erreur chargement données ${city.name}:`, err);
          this.isLoadingCityDetails.set(false);
        }
      });
  }

  /**
   * 🗺️ Initialiser la carte Leaflet
   */
  private initMap() {
    if (this.map || this.mapData().length === 0) return;

    console.log('🗺️ Initialisation de la carte avec', this.mapData().length, 'villes');

    // Déterminer le centre et le zoom initial
    const userCity = this.userCity();
    let center: [number, number];
    let zoom: number;

    if (userCity) {
      // Centrer sur la ville de l'utilisateur
      center = [userCity.latitude, userCity.longitude];
      zoom = 10;
      console.log('🎯 Carte centrée sur la ville de l\'utilisateur:', userCity.name);
    } else {
      // Par défaut, centrer sur l'Occitanie
      center = [43.6, 1.45];
      zoom = 7;
      console.log('🗺️ Carte centrée sur l\'Occitanie (par défaut)');
    }

    // Créer la carte
    this.map = L.map('map').setView(center, zoom);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      maxZoom: 19
    }).addTo(this.map);

    // Forcer la carte à recalculer ses dimensions
    setTimeout(() => {
      if (this.map) {
        this.map.invalidateSize();
      }
    }, 100);

    // Ajouter les marqueurs pour chaque ville
    this.addCityMarkers();

    // Ajouter un listener sur le zoom pour gérer la visibilité
    this.map.on('zoomend', () => this.updateMarkersVisibility());
  }

  /**
   * 📍 Ajouter les marqueurs des villes
   */
  private addCityMarkers() {
    if (!this.map) return;

    this.mapData().forEach(city => {
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
    });

    // Initialiser la visibilité des marqueurs
    this.updateMarkersVisibility();
  }

  /**
   * 👁️ Mettre à jour la visibilité des marqueurs selon le zoom
   */
  private updateMarkersVisibility() {
    if (!this.map) return;

    const zoom = this.map.getZoom();
    let shownCount = 0;

    this.cityMarkers.forEach((item) => {
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

              // Lazy loading : charger les données au clic
              this.loadCityDetails(city);

              // Zoomer sur la ville avec une animation fluide
              if (this.map) {
                this.map.flyTo([city.latitude, city.longitude], 12, {
                  duration: 1.5,
                  easeLinearity: 0.25
                });
              }

              console.log(`🖱️ Clic sur ${city.name}:`, city);
            });

            item.popupBound = true;
          }

          shownCount++;
        }
      } else {
        if (this.map!.hasLayer(marker)) {
          this.map!.removeLayer(marker);
        }
      }
    });

    console.log(`👁️ Zoom ${zoom}: ${shownCount} villes affichées sur ${this.cityMarkers.length}`);
  }

  /**
   * 🔍 Déterminer si une ville doit être affichée selon le zoom
   */
  private shouldShowCity(city: CityMapPoint, zoom: number): boolean {
    // Grandes villes -> Rouge
    if (zoom < 8) {
      return city.population > 100000;
    }

    // Grandes + moyennes villes → Rouge + bleu
    if (zoom < 10) {
      return city.population > 10000;
    }

    // Toutes les villes → Rouge + bleu + vert
    return true;
  }

  /**
   * 🎨 Obtenir la couleur selon la population
   */
  private getCityColor(city: CityMapPoint): string {
    if (city.population > 100000) return '#E91E63';
    if (city.population > 10000) return '#2196F3';
    return '#4CAF50';
  }

  /**
   * 📏 Obtenir le rayon selon la population
   */
  private getCityRadius(city: City): number {
    const minRadius = 2;
    const maxRadius = 8;
    const minPop = 100;
    const maxPop = 500000;

    const normalized = Math.log(city.population || minPop) / Math.log(maxPop);
    return minRadius + (maxRadius - minRadius) * normalized;
  }

}
