import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { AirQualityData, AirQualityIndex, AirQualityMeasurement } from '../models/data.model';
import { ApiConfigService } from './api';

@Injectable({ providedIn: 'root' })
export class AirQualityService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiConfigService);
  private readonly apiUrl = `${this.api.apiUrl}/air-quality`;

  getComplete(cityName: string): Observable<AirQualityData> {
    return this.http
      .get<AirQualityData>(`${this.apiUrl}/city/${cityName}/complete`, {
        withCredentials: true,
      });
  }

  getLatestIndex(cityName: string): Observable<AirQualityIndex | null> {
    return this.getComplete(cityName).pipe(map(data => data.latestIndex || null));
  }

  getLatestMeasurement(cityName: string): Observable<AirQualityMeasurement | null> {
    return this.getComplete(cityName).pipe(map(data => data.latestMeasurement || null));
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
}
