import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { AirQualityComplete, AirQualityIndex, AirQualityMeasurement } from '../models/data.model';

@Injectable({ providedIn: 'root' })
export class AirQualityService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/air-quality';

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

  private mapToComplete(data: any): AirQualityComplete {
    return {
      latestMeasurement: data.latestMeasurement,
      latestIndex: data.latestIndex,
      measurementHistory: data.measurementHistory || [],
      indexHistory: data.indexHistory || [],
    };
  }
}
