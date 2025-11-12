import { Injectable } from '@angular/core';
import {BehaviorSubject, concatMap, Observable, of, tap} from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { UserProfileResponse } from '../../core/models/user.model';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl = 'http://localhost:8080/api';

  private readonly _userProfileSubject = new BehaviorSubject<UserProfileResponse | null>(null);

  public readonly userProfile$ = this._userProfileSubject.asObservable();

  constructor(private http: HttpClient) {
    const storedProfile = localStorage.getItem('userProfile');
    if (storedProfile) {
      this._userProfileSubject.next(JSON.parse(storedProfile));
    }
  }

  get currentUserProfile(): UserProfileResponse | null {
    return this._userProfileSubject.value;
  }

  fetchUserProfile(): void {
    this.http.get<UserProfileResponse>(`${this.apiUrl}/profile`, { withCredentials: true }).subscribe({
      next: profile => {
        // Accepte que profile.user null (profil guest)
        if (profile && profile.user) {
          this.setUserProfile(profile);
        } else {
          this.setUserProfile(null);
        }
      },
      error: err => {
        // En cas d'erreur 401, considérer profil guest (null)
        if (err.status === 401) {
          this.setUserProfile(null);
        } else {
          console.error('Erreur fetching profile:', err);
          this.setUserProfile(null);
        }
      }
    });
  }

  setUserProfile(profile: UserProfileResponse | null): void {
    if (profile) {
      localStorage.setItem('userProfile', JSON.stringify(profile));
    } else {
      localStorage.removeItem('userProfile');
    }
    this._userProfileSubject.next(profile);
  }

  // L'authentification
  login(credentials: { username: string; password: string }): Observable<UserProfileResponse> {
    return new Observable(observer => {
      this.http.post<UserProfileResponse>(`${this.apiUrl}/login`, credentials, { withCredentials: true })
        .subscribe({
          next: profile => {
            this.setUserProfile(profile);
            // Re-synchronisation optionnelle pour garantir la cohérence cookie/vue
            this.fetchUserProfile();
            observer.next(profile);
            observer.complete();
          },
          error: err => {
            this.setUserProfile(null);
            observer.error(err);
            observer.complete();
          }
        });
    });
  }

  // Déconnexion
  logout(): Observable<void> {
    return this.http.get<void>(`${this.apiUrl}/logout`, { withCredentials: true }).pipe(
      tap(() => this.setUserProfile(null)), // Nettoie immédiatement le profil
      // ⚠️ Attendre un peu avant refetch pour laisser le cookie "guest" s'appliquer
      tap(() => setTimeout(() => this.fetchUserProfile(), 500)),
      catchError(() => {
        this.setUserProfile(null);
        return of(void 0);
      })
    );
  }

  // Vérififer si username ou l'email existe en base
  checkAvailability(username: string, email: string) {
    return this.http.get<{usernameTaken: boolean, emailTaken: boolean}>(
      `${this.apiUrl}/users/check`,
      { params: { username, email }, withCredentials: true }
    );
  }

  //Ajout nouveau user
  register(userData: any) {
    return this.http.post(`${this.apiUrl}/users/signup`, userData, { withCredentials: true });
  }

  //Mettre à jour les infos de l'user
  editUser(userId: number | null, payload: any) {
    return this.http.put<UserProfileResponse>(
      `${this.apiUrl}/users/${userId}`,
      payload,
      { withCredentials: true }
    ).pipe(
      tap(profile => this.setUserProfile(profile)) // met à jour localStorage et signal
    );
  }


  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete?id=${id}`, { withCredentials: true }).pipe(
      tap(() => setTimeout(() => window.location.reload(), 5))
    );
  }




  editAddress(id: number | null, payload: any) {
    return this.http.put(`${this.apiUrl}/address/${id}`, payload, { withCredentials: true });
  }

  getUsername() {
    return this._userProfileSubject.value ? this._userProfileSubject.value.user.username : null;
  }
}
