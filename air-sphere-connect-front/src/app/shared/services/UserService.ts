import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Favorite {
  id: number;
  cityName: string;
  createdAt: string;
  updatedAt: string;
  favoriteCategory: string;
}


export interface User {
  id: number;
  username: string;
  email: string;
  role: string[];
  address: {
    street: string;
    city: {
      name: string;
      postalCode: string;
    };
  };
  favorites: Favorite[];
}

export interface UserProfileResponse {
  role: string;
  user: User;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl = 'http://localhost:8080/api/guest-token';

  private readonly _userProfileSubject = new BehaviorSubject<UserProfileResponse | null>(null);

  public readonly userProfile$ = this._userProfileSubject.asObservable();

  constructor(private http: HttpClient) {}

  fetchUserProfile(): void {
    this.http.get<UserProfileResponse>('http://localhost:8080/api/profile', { withCredentials: true }).subscribe({
      next: profile => this._userProfileSubject.next(profile),
      error: () => {
        // fallback token invité si non connecté
        this.http.get<UserProfileResponse>(this.apiUrl, { withCredentials: true }).subscribe({
          next: guest => this._userProfileSubject.next(guest),
          error: () => this._userProfileSubject.next(null)
        });
      }
    });
  }


  setUserProfile(profile: UserProfileResponse): void {
    this._userProfileSubject.next(profile);
  }

  logout(): void {
    this._userProfileSubject.next(null);
    this.http.post('http://localhost:8080/api/logout', {}, { withCredentials: true }).subscribe({
      next: () => this.fetchUserProfile(),
      error: () => this._userProfileSubject.next(null)
    });
  }
}
