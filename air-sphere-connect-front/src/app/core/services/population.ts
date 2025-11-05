import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { PopulationData } from '../models/data.model';

@Injectable({ providedIn: 'root' })
export class PopulationService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/history';

  getHistory(cityName: string): Observable<PopulationData[]> {
    return this.http
      .get<any[]>(`${this.apiUrl}/${cityName}`, {
        withCredentials: true,
      })
      .pipe(map(data => data.map(this.mapToPopulation)));
  }

  getTopCities(limit: number): Observable<PopulationData[]> {
    return this.http
      .get<any[]>(`${this.apiUrl}/top/${limit}`, {
        withCredentials: true,
      })
      .pipe(map(data => data.map(this.mapToPopulation)));
  }

  private mapToPopulation(data: any): PopulationData {
    return {
      cityName: data.cityName,
      population: data.population,
      year: data.year,
      source: data.source,
    };
  }
}
