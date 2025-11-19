import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { AirQualityComplete, AirQualityIndex, AirQualityMeasurement, AirQualityData } from '../models/data.model';
import { ApiConfigService } from './api';

@Injectable({ providedIn: 'root' })
export class AirQualityService {
  private http = inject(HttpClient);
  private api = inject(ApiConfigService);
  private apiUrl = `${this.api.apiUrl}/air-quality`;

  getComplete(cityName: string): Observable<AirQualityComplete> {
    return this.http
      .get<any>(`${this.apiUrl}/city/${cityName}/complete`, {
        withCredentials: true,
      })
      .pipe(map(this.mapToComplete));
  }

  getLatestIndex(cityName: string): Observable<AirQualityIndex | null> {
    return this.getComplete(cityName).pipe(map(data => data.latestIndex));
  }

  getLatestMeasurement(cityName: string): Observable<AirQualityMeasurement | null> {
    return this.getComplete(cityName).pipe(map(data => data.latestMeasurement));
  }

  /**
   * Récupère les N plus grandes villes du département avec des données air quality
   * @param departmentCode Code du département (2 chiffres, ex: "34")
   * @param limit Nombre de villes à retourner (par défaut 2)
   */
  getTopCitiesInDepartment(departmentCode: string, limit: number = 2): Observable<AirQualityData[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<AirQualityData[]>(
      `${this.apiUrl}/department/${departmentCode}/top-cities`,
      { params, withCredentials: true }
    );
  }

  private mapToComplete(data: any): AirQualityComplete {
    return {
      latestMeasurement: data.latestMeasurement,
      latestIndex: data.latestIndex,
      measurementHistory: data.measurementHistory || [],
      indexHistory: data.indexHistory || [],
    };
  }
}
