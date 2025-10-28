import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {

  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  addFavorites(favoriteData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/favorites/new`, favoriteData, { withCredentials: true });
  }

  editFavorites(favoriteData: any, id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/favorites/${id}`, favoriteData, { withCredentials: true });
  }

  deleteFavorites(id :number) {
    return this.http.delete(`${this.apiUrl}/favorites/${id}`, { withCredentials: true });

  }
}
