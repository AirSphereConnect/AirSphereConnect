import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { City } from '../models/city.model';
import { ApiConfigService } from './api';

@Injectable({
  providedIn: 'root'
})
export class CityService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiConfigService);
  private readonly apiUrl = `${this.api.apiUrl}/cities`;

  /**
   * 🔍 Recherche partielle (autocomplétion)
   * Exemple: /api/cities/search-name?query=Toulouse
   */
  searchCities(query: string): Observable<City[]> {
    return this.http
      .get<City[]>(`${this.apiUrl}/search-name`, {
        params: { query }})
      .pipe(map(cities => cities.map(this.mapToCity)));
  }

  /**
   * 📍 Récupération d'une ville par son nom exact
   * Exemple: /api/cities/city?name=Toulouse
   */
  getByName(name: string): Observable<City> {
    return this.http
      .get<City>(`${this.apiUrl}/city`, {
        params: { name },
        withCredentials: true
      })
      .pipe(map(this.mapToCity));
  }

  /**
   * 📋 Récupération de toutes les villes
   * Exemple: /api/cities
   */
  getAll(): Observable<City[]> {
    return this.http
      .get<City[]>(this.apiUrl, { withCredentials: true })
      .pipe(map(cities => cities.map(this.mapToCity)));
  }


  /**
   * 📊 Top N villes d'un area code
   * Exemple: /api/cities/area/343/top/10
   */
  getTopCitiesByArea(areaCode: string, limit: number): Observable<City[]> {
    return this.http
      .get<City[]>(`${this.apiUrl}/area/${areaCode}/top/${limit}`, { withCredentials: true })
      .pipe(map(cities => cities.map(this.mapToCity)));
  }

  /**
   * 🗂️ Mapper les données de l'API vers le modèle City
   */
  private mapToCity(data: City): City {
    return {
      id: data.id,
      name: data.name,
      inseeCode: data.inseeCode,
      areaCode: data.areaCode,
      postalCode: data.postalCode,
      latitude: data.latitude,
      longitude: data.longitude,
      population: data.population,
      departmentName: data.departmentName
    };
  }
}
