import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { PopulationData } from '../models/data.model';
import { ApiConfigService } from './api';

@Injectable({ providedIn: 'root' })
export class PopulationService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiConfigService);
  private readonly apiUrl = `${this.api.apiUrl}/history`;

  getHistory(cityName: string): Observable<PopulationData[]> {
    return this.http
      .get<PopulationData[]>(`${this.apiUrl}/${cityName}`, {
        withCredentials: true,
      })
      .pipe(map(data => data.map(this.mapToPopulation)));
  }

  getTopCities(limit: number): Observable<PopulationData[]> {
    return this.http
      .get<PopulationData[]>(`${this.apiUrl}/top/${limit}`, {
        withCredentials: true,
      })
      .pipe(map(data => data.map(this.mapToPopulation)));
  }

  private mapToPopulation(data: PopulationData): PopulationData {
    return {
      cityName: data.cityName,
      population: data.population,
      year: data.year,
      source: data.source,
    };
  }
}
