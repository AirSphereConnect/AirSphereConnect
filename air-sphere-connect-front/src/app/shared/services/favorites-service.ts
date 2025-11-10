import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of, tap} from 'rxjs';
import {UserProfileResponse} from '../../core/models/user.model';
import {HttpClient} from '@angular/common/http';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {

  private readonly apiUrl = 'http://localhost:8080/api';
  private readonly _userProfileSubject = new BehaviorSubject<UserProfileResponse | null>(null);
  public readonly userProfile$ = this._userProfileSubject.asObservable();

  constructor(private http: HttpClient) {
    const storedProfile = localStorage.getItem('userProfile');
    if (storedProfile) {
      this._userProfileSubject.next(JSON.parse(storedProfile));
    }
  }

  fetchUserProfile(): void {
    this.http.get<UserProfileResponse>(`${this.apiUrl}/profile`, { withCredentials: true }).subscribe({
      next: profile => this.setUserProfile(profile),
      error: () => this.setUserProfile(null)
    });
  }

  getUserId(): number | null {
    console.log(this._userProfileSubject.value ? this._userProfileSubject.value.user.id : null)
    return this._userProfileSubject.value ? this._userProfileSubject.value.user.id : null
  }

  setUserProfile(profile: UserProfileResponse | null): void {
    if (profile) {
      localStorage.setItem('userProfile', JSON.stringify(profile));
    } else {
      localStorage.removeItem('userProfile');
    }
    this._userProfileSubject.next(profile);
  }

  //Ajout nouveau favoris
  addFavorites(userData: any): Observable<any> {
    const id = this.getUserId();
    if (!id) {
      throw new Error('User id n\'est pas reconnus');
    }
    return this.http.post(`${this.apiUrl}/favorites/new/${id}`, userData, { withCredentials: true });
  }

  //Mettre à jour les infos de l'user
  editFavorites(userData: any, id: number) {
    return this.http.put(`${this.apiUrl}/favorites/${id}`, userData, { withCredentials: true });
  }

  deleteFavorites(id :number) {
    return this.http.delete(`${this.apiUrl}/favorites/${id}`, { withCredentials: true });

  }
}
