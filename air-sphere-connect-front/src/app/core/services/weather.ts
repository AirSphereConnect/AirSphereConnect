import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { WeatherMeasurement } from '../models/data.model';
import { ApiConfigService } from './api';

@Injectable({ providedIn: 'root' })
export class WeatherService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiConfigService);
  private readonly apiUrl = `${this.api.apiUrl}/weather`;

  getHistory(cityId: number): Observable<WeatherMeasurement[]> {
    return this.http
      .get<any[]>(`${this.apiUrl}/city/history/${cityId}`, {
        withCredentials: true, // ✅ essentiel pour le cookie de session
      })
      .pipe(map(data => data.map(this.mapToWeather)));
  }

  getLatest(cityId: number): Observable<WeatherMeasurement | null> {
    return this.getHistory(cityId).pipe(
      map(history => (history.length > 0 ? history[0] : null))
    );
  }

  private mapToWeather(data: any): WeatherMeasurement {
    return {
      id: data.id,
      temperature: data.temperature,
      humidity: data.humidity,
      pressure: data.pressure,
      windSpeed: data.windSpeed,
      windDirection: data.windDirection,
      measuredAt: new Date(data.measuredAt),
      message: data.message,
    };
  }
}
