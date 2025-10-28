import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AlertsService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}


  editAlerts(payload: { activeAlert: any; cityId: number }, editingAlertsId: number) {
    return this.http.put(`${this.apiUrl}/favorites/${editingAlertsId}`, payload, { withCredentials: true });

  }

  addAlerts(payload: { activeAlert: any; cityId: number }) {
    return this.http.post(`${this.apiUrl}/alerts/new`, payload, { withCredentials: true });

  }
}
