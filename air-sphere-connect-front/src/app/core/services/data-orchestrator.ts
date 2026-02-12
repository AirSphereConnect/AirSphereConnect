import { Injectable, inject } from '@angular/core';
import {forkJoin, Observable, of, switchMap, tap} from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { CityService } from './city';
import { WeatherService } from './weather';
import { AirQualityService } from './air-quality';
import { PopulationService } from './population';
import {
  DashboardData,
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
   * 📊 DASHBOARD - Données complètes pour une ville
   */
  loadDashboardData(cityName: string): Observable<DashboardData> {
    return this.cityService.getByName(cityName).pipe(
      switchMap(city => {
        console.log('📡 Ville récupérée du backend:', city);
        return forkJoin({
          city: of(city),
          weatherHistory: this.weatherService.getHistory(city.id).pipe(tap(data => console.log('🌡️ weatherHistory', data))),
          airQuality: this.airQualityService.getComplete(cityName).pipe(tap(data => console.log('🏭 airQuality', data))),
          populationHistory: this.populationService.getHistory(cityName).pipe(tap(data => console.log('👥 populationHistory', data)))
        });
      })
    );
  }

  /**
   * ⭐ FAVORIS - Snapshot du jour pour UNE ville
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
   * ⭐ FAVORIS - Snapshots pour PLUSIEURS villes
   */
  loadMultipleFavoritesSnapshots(cityNames: string[]): Observable<FavoriteCityData[]> {
    return forkJoin(
      cityNames.map(name => this.loadFavoriteCitySnapshot(name))
    );
  }

  /**
   * 📜 HISTORIQUE - Données historiques complètes pour tableau avec cache
   */
  loadCityHistoryTable(cityName: string): Observable<CityHistoryData> {
    // Vérifier si les données sont en cache
    if (!this.historyCache.has(cityName)) {
      // Créer l'observable et le mettre en cache avec shareReplay
      const historyData$ = this.cityService.getByName(cityName).pipe(
        switchMap(city =>
          forkJoin({
            city: of(city),
            weatherHistory: this.weatherService.getHistory(city.id),
            airQuality: this.airQualityService.getComplete(cityName)
          })
        ),
        map(data => {
          console.log('📦 [HISTORY] Backend data received:');
          console.log('  - Weather history:', data.weatherHistory.length, 'entries');
          console.log('  - Measurement history:', data.airQuality.measurementHistory?.length || 0, 'entries');
          console.log('  - Index history:', data.airQuality.indexHistory?.length || 0, 'entries');
          console.log('  - Latest measurement:', data.airQuality.latestMeasurement);
          console.log('  - Latest index:', data.airQuality.latestIndex);

          if (data.weatherHistory.length > 0) {
            console.log('🔍 Weather measuredAt type:', typeof data.weatherHistory[0].measuredAt, data.weatherHistory[0].measuredAt);
          }
          if (data.airQuality.measurementHistory && data.airQuality.measurementHistory.length > 0) {
            console.log('🔍 Measurement measuredAt type:', typeof data.airQuality.measurementHistory[0].measuredAt, data.airQuality.measurementHistory[0].measuredAt);
          }

          // Toujours ajouter latestMeasurement car il peut avoir des valeurs différentes/plus récentes
          const measurements = [...(data.airQuality.measurementHistory || [])];
          if (data.airQuality.latestMeasurement) {
            measurements.push(data.airQuality.latestMeasurement);
            console.log('✅ [HISTORY] Added latestMeasurement to array');
          }

          // Combiner les historiques par date
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
        shareReplay({ bufferSize: 1, refCount: true }) // Cache avec shareReplay
      );

      this.historyCache.set(cityName, historyData$);
    }

    return this.historyCache.get(cityName)!;
  }

  /**
   * Invalider le cache d'historique pour une ville (appelé lors du refresh manuel)
   */
  clearHistoryCache(cityName?: string) {
    if (cityName) {
      this.historyCache.delete(cityName);
      console.log(`🗑️ Cache invalidé pour ${cityName}`);
    } else {
      this.historyCache.clear();
      console.log('🗑️ Cache complet invalidé');
    }
  }

  /**
   * 🔧 Helper - Fusionner les données par date
   */
  private mergeDailyData(
    weatherHistory: any[],
    airMeasurements: any[],
    airIndexes: any[]
  ): CityDailySnapshot[] {
    console.log('\n🔧 [MERGE] Starting merge process...');

    const dataByDate = new Map<string, CityDailySnapshot>();

    this.addWeatherData(dataByDate, weatherHistory);
    this.addAirMeasurements(dataByDate, airMeasurements);
    this.addAirIndexes(dataByDate, airIndexes);

    return this.sortAndLogResults(dataByDate);
  }

  /**
   * Ajouter les données météo à la map
   */
  private addWeatherData(dataByDate: Map<string, CityDailySnapshot>, weatherHistory: any[]): void {
    for (const w of weatherHistory) {
      const dateKey = this.formatDate(new Date(w.measuredAt));
      if (!dataByDate.has(dateKey)) {
        dataByDate.set(dateKey, this.createSnapshot(dateKey, { weather: w }));
      }
    }
  }

  /**
   * Ajouter les mesures de qualité de l'air à la map
   */
  private addAirMeasurements(dataByDate: Map<string, CityDailySnapshot>, airMeasurements: any[]): void {
    const measurementsByDate = this.groupMeasurementsByDate(airMeasurements);

    for (const [dateKey, measurements] of measurementsByDate) {
      const averaged = this.averageMeasurements(measurements);
      this.logDebugMeasurements(dateKey, measurements, averaged);

      const existing = dataByDate.get(dateKey);
      if (existing) {
        existing.airMeasurement = averaged;
      } else {
        dataByDate.set(dateKey, this.createSnapshot(dateKey, { airMeasurement: averaged }));
      }
    }
  }

  /**
   * Ajouter les index de qualité de l'air à la map
   */
  private addAirIndexes(dataByDate: Map<string, CityDailySnapshot>, airIndexes: any[]): void {
    for (const index of airIndexes) {
      const dateKey = this.extractDateKey(index.measuredAt);
      this.logDebugIfTestDate(dateKey, `Adding index (type: ${typeof index.measuredAt})`, index);

      const existing = dataByDate.get(dateKey);
      if (existing) {
        existing.airIndex = index;
      } else {
        dataByDate.set(dateKey, this.createSnapshot(dateKey, { airIndex: index }));
      }
    }
  }

  /**
   * Grouper les mesures par date
   */
  private groupMeasurementsByDate(airMeasurements: any[]): Map<string, any[]> {
    const measurementsByDate = new Map<string, any[]>();

    for (const m of airMeasurements) {
      const dateKey = this.extractDateKey(m.measuredAt);
      this.logDebugIfTestDate(dateKey, `Found measurement with measuredAt=${m.measuredAt}`, m);

      if (!measurementsByDate.has(dateKey)) {
        measurementsByDate.set(dateKey, []);
      }
      measurementsByDate.get(dateKey)!.push(m);
    }

    return measurementsByDate;
  }

  /**
   * Extraire une clé de date depuis différents formats (Date object ou string)
   */
  private extractDateKey(measuredAt: any): string {
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

  /**
   * Créer un snapshot vide avec date et données partielles
   */
  private createSnapshot(dateKey: string, data: Partial<CityDailySnapshot>): CityDailySnapshot {
    const [year, month, day] = dateKey.split('-').map(Number);
    return {
      date: new Date(year, month - 1, day, 0, 0, 0, 0),
      weather: data.weather || null,
      airMeasurement: data.airMeasurement || null,
      airIndex: data.airIndex || null
    };
  }

  /**
   * Trier les résultats et logger les détails de debug
   */
  private sortAndLogResults(dataByDate: Map<string, CityDailySnapshot>): CityDailySnapshot[] {
    const result = Array.from(dataByDate.values())
      .sort((a, b) => b.date.getTime() - a.date.getTime());

    this.logFinalSnapshots(result);
    return result;
  }

  /**
   * Logger les snapshots finaux pour les dates de test
   */
  private logFinalSnapshots(result: CityDailySnapshot[]): void {
    const testDates = ['2025-11-14', '2025-11-13'];

    for (const testDate of testDates) {
      const snapshot = result.find(r => this.formatDate(r.date) === testDate);
      if (snapshot) {
        console.log(`\n✅ [${testDate.slice(5)}] Final snapshot:`, {
          date: snapshot.date,
          hasWeather: !!snapshot.weather,
          airMeasurement: snapshot.airMeasurement,
          airIndex: snapshot.airIndex
        });
      } else {
        console.log(`\n⚠️ [${testDate.slice(5)}] NO SNAPSHOT FOUND in final result!`);
      }
    }
  }

  /**
   * Logger les détails de debug pour les mesures (seulement pour dates de test)
   */
  private logDebugMeasurements(dateKey: string, measurements: any[], averaged: any): void {
    if (this.isTestDate(dateKey)) {
      console.log(`📊 [${dateKey}] Processing ${measurements.length} measurements:`, measurements);
      console.log(`📊 [${dateKey}] Averaged result:`, averaged);
    }
  }

  /**
   * Logger si la date est une date de test
   */
  private logDebugIfTestDate(dateKey: string, message: string, data?: any): void {
    if (this.isTestDate(dateKey)) {
      console.log(`📊 [${dateKey}] ${message}`, data || '');
    }
  }

  /**
   * Vérifier si c'est une date de test (pour réduire les logs)
   */
  private isTestDate(dateKey: string): boolean {
    return ['2025-11-14', '2025-11-13', '2025-11-15'].includes(dateKey);
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Normaliser une date string (YYYY-MM-DD) vers minuit
   */
  private normalizeToMidnight(dateString: string): Date {
    // Créer une date à minuit dans le fuseau horaire local
    const [year, month, day] = dateString.split('-').map(Number);
    return new Date(year, month - 1, day, 0, 0, 0, 0);
  }

  /**
   * Calculer la moyenne des mesures air de plusieurs stations/heures
   */
  private averageMeasurements(measurements: any[]): any {
    if (measurements.length === 0) return null;
    if (measurements.length === 1) return measurements[0];

    // Collecter toutes les valeurs par polluant
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

    // Calculer les moyennes avec arrondi à 1 décimale
    const average = (values: number[]) =>
      values.length > 0
        ? Math.round((values.reduce((a, b) => a + b, 0) / values.length) * 10) / 10
        : null;

    // Utiliser le premier measurement comme base
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
   * (même méthode, différente présentation dans le composant)
   */
  loadAdminDashboardData(cityName: string): Observable<DashboardData> {
    return this.loadDashboardData(cityName);
  }
}
