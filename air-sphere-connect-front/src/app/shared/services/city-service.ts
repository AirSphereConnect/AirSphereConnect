import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiConfigService } from '../../core/services/api';

@Injectable({
  providedIn: 'root'
})
export class CityService {

  private readonly http = inject(HttpClient);
  private readonly apiConfig = inject(ApiConfigService);
  private readonly apiUrl = `${this.apiConfig.apiUrl}`;

  constructor() {}

  searchCities(query: string) {
    return this.http.get<{cityName: string, cityCode: string}[]>(
      `${this.apiUrl}/cities/search-name`,
      { params: { query } }
    );
  }

}
