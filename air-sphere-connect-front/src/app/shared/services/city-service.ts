import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CityService {

  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  searchCities(query: string) {
    return this.http.get<{cityName: string, cityCode: string}[]>(
      `${this.apiUrl}/cities/search-name`,
      { params: { query } }
    );
  }

}
