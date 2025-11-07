import { Injectable, inject } from '@angular/core';
import {forkJoin, Observable, of, switchMap, tap} from 'rxjs';
import { map } from 'rxjs/operators';
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
   * 📜 HISTORIQUE - Données historiques complètes pour tableau
   */
  loadCityHistoryTable(cityName: string): Observable<CityHistoryData> {
    return this.cityService.getByName(cityName).pipe(
      switchMap(city =>
        forkJoin({
          city: of(city),
          weatherHistory: this.weatherService.getHistory(city.id),
          airQuality: this.airQualityService.getComplete(cityName)
        })
      ),
      map(data => {
        // Combiner les historiques par date
        const dailyData = this.mergeDailyData(
          data.weatherHistory,
          data.airQuality.measurementHistory,
          data.airQuality.indexHistory
        );

        return {
          city: data.city,
          dailySnapshots: dailyData
        };
      })
    );
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

    // Grouper météo
    weatherHistory.forEach(w => {
      const date = this.formatDate(w.measuredAt);
      if (!dataByDate.has(date)) {
        dataByDate.set(date, {
          date: new Date(w.measuredAt),
          weather: w,
          airMeasurement: null,
          airIndex: null
        });
      }
    });

    // Grouper mesures air
    airMeasurements.forEach(m => {
      const date = this.formatDate(new Date(m.measuredAt));
      const existing = dataByDate.get(date);
      if (existing) {
        existing.airMeasurement = m;
      } else {
        dataByDate.set(date, {
          date: new Date(m.measuredAt),
          weather: null,
          airMeasurement: m,
          airIndex: null
        });
      }
    });

    // Grouper index air
    airIndexes.forEach(i => {
      const date = this.formatDate(new Date(i.measuredAt));
      const existing = dataByDate.get(date);
      if (existing) {
        existing.airIndex = i;
      }
    });

    // Convertir en tableau et trier par date décroissante
    return Array.from(dataByDate.values())
      .sort((a, b) => b.date.getTime() - a.date.getTime());
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  /**
   * 👑 ADMIN - Même chose que dashboard mais en read-only
   * (même méthode, différente présentation dans le composant)
   */
  loadAdminDashboardData(cityName: string): Observable<DashboardData> {
    return this.loadDashboardData(cityName);
  }
}
