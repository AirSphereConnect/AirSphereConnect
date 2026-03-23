import { Injectable, inject } from '@angular/core';
import {forkJoin, Observable, of, switchMap} from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { CityService } from './city';
import { WeatherService } from './weather';
import { AirQualityService } from './air-quality';
import { PopulationService } from './population';
import {
  DashboardData,
  WeatherMeasurement,
  AirQualityMeasurement,
  AirQualityIndex,
} from '../models/data.model';
import {
  FavoriteCityData,
  CityHistoryData,
  CityDailySnapshot
} from '../models/city.model'

@Injectable({ providedIn: 'root' })
export class DataOrchestratorService {
  private readonly cityService = inject(CityService);
  private readonly weatherService = inject(WeatherService);
  private readonly airQualityService = inject(AirQualityService);
  private readonly populationService = inject(PopulationService);

  // Cache pour les données d'historique par ville
  private readonly historyCache = new Map<string, Observable<CityHistoryData>>();

  /**
   * Données environnementales et démographiques complètes pour une ville
   * @param cityName Nom de la ville à charger
   * @returns Observable de DashboardData avec les données combinées
   */
  loadDashboardData(cityName: string): Observable<DashboardData> {
    return this.cityService.getByName(cityName).pipe(
      switchMap(city => {
        return forkJoin({
          city: of(city),
          weatherHistory: this.weatherService.getHistory(city.id),
          airQuality: this.airQualityService.getComplete(cityName),
          populationHistory: this.populationService.getHistory(cityName)
        });
      })
    );
  }

  /**
   * Historique - Charger les données d'historique pour une ville avec cache et fusion intelligente
   * @param cityName Nom de la ville à charger
   * @returns Observable de CityHistoryData avec les données combinées et fusionnées par date
   */
  loadCityHistoryTable(cityName: string): Observable<CityHistoryData> {
    if (!this.historyCache.has(cityName)) {
      const historyData$ = this.cityService.getByName(cityName).pipe(
        switchMap(city =>
          forkJoin({
            city: of(city),
            weatherHistory: this.weatherService.getHistory(city.id),
            airQuality: this.airQualityService.getComplete(cityName)
          })
        ),
        map(data => {
          const measurements = [...(data.airQuality.measurementHistory || [])];
          if (data.airQuality.latestMeasurement) {
            measurements.push(data.airQuality.latestMeasurement);
          }

          const dailyData = this.mergeDailyData(
            data.weatherHistory,
            measurements,
            data.airQuality.indexHistory || []
          );

          return {
            city: data.city,
            dailySnapshots: dailyData
          };
        }),
        shareReplay({ bufferSize: 1, refCount: true })
      );

      this.historyCache.set(cityName, historyData$);
    }

    return this.historyCache.get(cityName)!;
  }

  /**
   * Favoris - Snapshot rapide pour une ville
   * @param cityName Nom de la ville à charger
   * @returns Observable de FavoriteCityData avec les données combinées
   */
  loadFavoriteCitySnapshot(cityName: string): Observable<FavoriteCityData> {
    return this.cityService.getByName(cityName).pipe(
      switchMap(city =>
        forkJoin({
          city: of(city),
          todayWeather: this.weatherService.getLatest(city.id),
          airQualityToday: this.airQualityService.getLatestIndex(cityName),
          airQualityMeasurement: this.airQualityService.getLatestMeasurement(cityName)
        })
      ),
      map(data => ({
        city: data.city,
        weather: data.todayWeather,
        airQualityIndex: data.airQualityToday,
        airQualityMeasurement: data.airQualityMeasurement
      }))
    );
  }

  /**
   * Favoris - Charger plusieurs snapshots en parallèle pour le tableau de favoris
   */
  loadMultipleFavoritesSnapshots(cityNames: string[]): Observable<FavoriteCityData[]> {
    return forkJoin(
      cityNames.map(name => this.loadFavoriteCitySnapshot(name))
    );
  }

  /**
   * Invalider le cache d'historique pour une ville (appelé lors du refresh manuel)
   */
  clearHistoryCache(cityName?: string) {
    if (cityName) {
      this.historyCache.delete(cityName);
    } else {
      this.historyCache.clear();
    }
  }

  /**
   * Fusionner les données météo, mesures et index par date pour créer des snapshots quotidiens
   */
  private mergeDailyData(
    weatherHistory: WeatherMeasurement[],
    airMeasurements: AirQualityMeasurement[],
    airIndexes: AirQualityIndex[]
  ): CityDailySnapshot[] {
    const dataByDate = new Map<string, CityDailySnapshot>();

    this.addWeatherData(dataByDate, weatherHistory);
    this.addAirMeasurements(dataByDate, airMeasurements);
    this.addAirIndexes(dataByDate, airIndexes);

    return Array.from(dataByDate.values())
      .sort((a, b) => b.date.getTime() - a.date.getTime());
  }

  private addWeatherData(dataByDate: Map<string, CityDailySnapshot>, weatherHistory: WeatherMeasurement[]): void {
    for (const w of weatherHistory) {
      const dateKey = this.formatDate(new Date(w.measuredAt));
      if (!dataByDate.has(dateKey)) {
        dataByDate.set(dateKey, this.createSnapshot(dateKey, { weather: w }));
      }
    }
  }

  private addAirMeasurements(dataByDate: Map<string, CityDailySnapshot>, airMeasurements: AirQualityMeasurement[]): void {
    const measurementsByDate = this.groupMeasurementsByDate(airMeasurements);

    for (const [dateKey, measurements] of measurementsByDate) {
      const averaged = this.averageMeasurements(measurements);
      const existing = dataByDate.get(dateKey);
      if (existing) {
        existing.airMeasurement = averaged;
      } else {
        dataByDate.set(dateKey, this.createSnapshot(dateKey, { airMeasurement: averaged }));
      }
    }
  }

  private addAirIndexes(dataByDate: Map<string, CityDailySnapshot>, airIndexes: AirQualityIndex[]): void {
    for (const index of airIndexes) {
      const dateKey = this.extractDateKey(index.measuredAt);
      const existing = dataByDate.get(dateKey);
      if (existing) {
        existing.airIndex = index;
      } else {
        dataByDate.set(dateKey, this.createSnapshot(dateKey, { airIndex: index }));
      }
    }
  }

  private groupMeasurementsByDate(airMeasurements: AirQualityMeasurement[]): Map<string, AirQualityMeasurement[]> {
    const measurementsByDate = new Map<string, AirQualityMeasurement[]>();

    for (const m of airMeasurements) {
      const dateKey = this.extractDateKey(m.measuredAt);
      if (!measurementsByDate.has(dateKey)) {
        measurementsByDate.set(dateKey, []);
      }
      measurementsByDate.get(dateKey)!.push(m);
    }

    return measurementsByDate;
  }

  private extractDateKey(measuredAt: string | Date): string {
    if (typeof measuredAt === 'string') {
      return measuredAt.split('T')[0];
    }

    if (measuredAt instanceof Date) {
      const year = measuredAt.getUTCFullYear();
      const month = String(measuredAt.getUTCMonth() + 1).padStart(2, '0');
      const day = String(measuredAt.getUTCDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }

    return this.formatDate(new Date(measuredAt));
  }

  private createSnapshot(dateKey: string, data: Partial<Pick<CityDailySnapshot, 'weather' | 'airMeasurement' | 'airIndex'>>): CityDailySnapshot {
    const [year, month, day] = dateKey.split('-').map(Number);
    return {
      date: new Date(year, month - 1, day, 0, 0, 0, 0),
      weather: data.weather || null,
      airMeasurement: data.airMeasurement || null,
      airIndex: data.airIndex || null
    };
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private averageMeasurements(measurements: AirQualityMeasurement[]): AirQualityMeasurement {
    if (measurements.length === 0) return null;
    if (measurements.length === 1) return measurements[0];

    const pm25Values: number[] = [];
    const pm10Values: number[] = [];
    const no2Values: number[] = [];
    const o3Values: number[] = [];
    const so2Values: number[] = [];

    for (const m of measurements) {
      if (m.pm25 != null) pm25Values.push(m.pm25);
      if (m.pm10 != null) pm10Values.push(m.pm10);
      if (m.no2 != null) no2Values.push(m.no2);
      if (m.o3 != null) o3Values.push(m.o3);
      if (m.so2 != null) so2Values.push(m.so2);
    }

    const average = (values: number[]) =>
      values.length > 0
        ? Math.round((values.reduce((a, b) => a + b, 0) / values.length) * 10) / 10
        : null;

    return {
      ...measurements[0],
      pm25: average(pm25Values),
      pm10: average(pm10Values),
      no2: average(no2Values),
      o3: average(o3Values),
      so2: average(so2Values)
    };
  }

  /**
   * ADMIN - Même chose que dashboard mais en read-only
   */
  loadAdminDashboardData(cityName: string): Observable<DashboardData> {
    return this.loadDashboardData(cityName);
  }
}