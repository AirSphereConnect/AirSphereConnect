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
  private cityService = inject(CityService);
  private weatherService = inject(WeatherService);
  private airQualityService = inject(AirQualityService);
  private populationService = inject(PopulationService);

  // Cache pour les données d'historique par ville
  private historyCache = new Map<string, Observable<CityHistoryData>>();

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
            data.airQuality.indexHistory
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
    const dataByDate = new Map<string, CityDailySnapshot>();

    console.log('\n🔧 [MERGE] Starting merge process...');

    // Grouper météo
    weatherHistory.forEach(w => {
      // Weather measuredAt est un objet Date déjà en local time
      const weatherDate = new Date(w.measuredAt);
      const dateKey = this.formatDate(weatherDate);

      if (!dataByDate.has(dateKey)) {
        // ⚠️ IMPORTANT: Créer une date à minuit LOCAL pour éviter les problèmes de timezone
        const [year, month, day] = dateKey.split('-').map(Number);
        dataByDate.set(dateKey, {
          date: new Date(year, month - 1, day, 0, 0, 0, 0),
          weather: w,
          airMeasurement: null,
          airIndex: null
        });
      }
    });

    // Grouper mesures air - Agréger par jour
    const measurementsByDate = new Map<string, any[]>();
    airMeasurements.forEach(m => {
      // ⚠️ PROBLEM: Angular HttpClient convertit automatiquement "2025-11-15T00:00:00" en Date object
      // qui est interprété comme UTC, donc 2025-11-15T00:00:00 UTC = 2025-11-15T01:00:00 Europe/Paris
      // Quand on fait formatDate(), ça extrait la date locale (15/11) mais le jour affiché est décalé
      // SOLUTION: Si c'est un Date object, extraire la date UTC au lieu de locale
      let dateKey: string;
      if (typeof m.measuredAt === 'string') {
        dateKey = m.measuredAt.split('T')[0];
      } else if (m.measuredAt instanceof Date) {
        // Extraire la date UTC (la "vraie" date stockée en DB)
        const year = m.measuredAt.getUTCFullYear();
        const month = String(m.measuredAt.getUTCMonth() + 1).padStart(2, '0');
        const day = String(m.measuredAt.getUTCDate()).padStart(2, '0');
        dateKey = `${year}-${month}-${day}`;
      } else {
        dateKey = this.formatDate(new Date(m.measuredAt));
      }

      if (dateKey === '2025-11-14' || dateKey === '2025-11-13' || dateKey === '2025-11-15') {
        console.log(`📊 [${dateKey}] Found measurement with measuredAt=${m.measuredAt} (type: ${typeof m.measuredAt}):`, m);
      }

      if (!measurementsByDate.has(dateKey)) {
        measurementsByDate.set(dateKey, []);
      }
      measurementsByDate.get(dateKey)!.push(m);
    });

    // Pour chaque jour, calculer la moyenne des mesures
    measurementsByDate.forEach((measurements, dateKey) => {
      const averaged = this.averageMeasurements(measurements);

      if (dateKey === '2025-11-14' || dateKey === '2025-11-13' || dateKey === '2025-11-15') {
        console.log(`📊 [${dateKey}] Processing ${measurements.length} measurements:`, measurements);
        console.log(`📊 [${dateKey}] Averaged result:`, averaged);
      }

      const existing = dataByDate.get(dateKey);
      if (existing) {
        console.log(`📊 [${dateKey}] Adding measurement to EXISTING entry (has weather: ${!!existing.weather})`);
        existing.airMeasurement = averaged;
      } else {
        console.log(`📊 [${dateKey}] Creating NEW entry with measurement only`);
        // ⚠️ IMPORTANT: Créer une date à minuit LOCAL pour éviter les problèmes de timezone
        const [year, month, day] = dateKey.split('-').map(Number);
        dataByDate.set(dateKey, {
          date: new Date(year, month - 1, day, 0, 0, 0, 0),
          weather: null,
          airMeasurement: averaged,
          airIndex: null
        });
      }
    });

    // Grouper index air
    airIndexes.forEach(i => {
      // ⚠️ SAME PROBLEM: Angular HttpClient converts dates to Date objects interpreted as UTC
      let dateKey: string;
      if (typeof i.measuredAt === 'string') {
        dateKey = i.measuredAt.split('T')[0];
      } else if (i.measuredAt instanceof Date) {
        // Extraire la date UTC (la "vraie" date stockée en DB)
        const year = i.measuredAt.getUTCFullYear();
        const month = String(i.measuredAt.getUTCMonth() + 1).padStart(2, '0');
        const day = String(i.measuredAt.getUTCDate()).padStart(2, '0');
        dateKey = `${year}-${month}-${day}`;
      } else {
        dateKey = this.formatDate(new Date(i.measuredAt));
      }

      if (dateKey === '2025-11-14' || dateKey === '2025-11-13' || dateKey === '2025-11-15') {
        console.log(`📊 [${dateKey}] Adding index (type: ${typeof i.measuredAt}):`, i);
      }

      const existing = dataByDate.get(dateKey);
      if (existing) {
        existing.airIndex = i;
      } else {
        // ⚠️ IMPORTANT: Créer une date à minuit LOCAL pour éviter les problèmes de timezone
        const [year, month, day] = dateKey.split('-').map(Number);
        dataByDate.set(dateKey, {
          date: new Date(year, month - 1, day, 0, 0, 0, 0),
          weather: null,
          airMeasurement: null,
          airIndex: i
        });
      }
    });

    // Convertir en tableau et trier par date décroissante
    const result = Array.from(dataByDate.values())
      .sort((a, b) => b.date.getTime() - a.date.getTime());

    // Log 13/11 and 14/11 final state
    const nov14 = result.find(r => this.formatDate(r.date) === '2025-11-14');
    const nov13 = result.find(r => this.formatDate(r.date) === '2025-11-13');

    if (nov14) {
      console.log('\n✅ [14/11] Final snapshot:', {
        date: nov14.date,
        hasWeather: !!nov14.weather,
        airMeasurement: nov14.airMeasurement,
        airIndex: nov14.airIndex
      });
    }

    if (nov13) {
      console.log('\n✅ [13/11] Final snapshot:', {
        date: nov13.date,
        hasWeather: !!nov13.weather,
        airMeasurement: nov13.airMeasurement,
        airIndex: nov13.airIndex
      });
    } else {
      console.log('\n⚠️ [13/11] NO SNAPSHOT FOUND in final result!');
    }

    return result;
  }

  private formatDate(date: Date): string {
    // ⚠️ IMPORTANT: Utiliser l'heure locale au lieu d'UTC pour éviter les problèmes de timezone
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * 📅 Normaliser une date string (YYYY-MM-DD) vers minuit
   */
  private normalizeToMidnight(dateString: string): Date {
    // Créer une date à minuit dans le fuseau horaire local
    const [year, month, day] = dateString.split('-').map(Number);
    return new Date(year, month - 1, day, 0, 0, 0, 0);
  }

  /**
   * 🧮 Calculer la moyenne des mesures air de plusieurs stations/heures
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

    measurements.forEach(m => {
      if (m.pm25 != null) pm25Values.push(m.pm25);
      if (m.pm10 != null) pm10Values.push(m.pm10);
      if (m.no2 != null) no2Values.push(m.no2);
      if (m.o3 != null) o3Values.push(m.o3);
      if (m.so2 != null) so2Values.push(m.so2);
    });

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
   * 👑 ADMIN - Même chose que dashboard mais en read-only
   * (même méthode, différente présentation dans le composant)
   */
  loadAdminDashboardData(cityName: string): Observable<DashboardData> {
    return this.loadDashboardData(cityName);
  }
}
