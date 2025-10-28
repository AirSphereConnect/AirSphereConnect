import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AlertsService {
  private readonly apiUrl = 'http://localhost:8080/api/alert/configurations';

  constructor(private http: HttpClient) {}


  editAlerts(payload: { enabled: boolean; cityId: number | null }, editingAlertsId: number) {
    return this.http.put(`${this.apiUrl}/${editingAlertsId}`, payload, { withCredentials: true });

  }

  addAlerts(payload: { enabled: boolean; cityId: number | null }) {
    return this.http.post(`${this.apiUrl}`, payload, { withCredentials: true });
  }

  deleteAlerts(editingAlertsId: number) {
    return this.http.delete(`${this.apiUrl}/${editingAlertsId}`, { withCredentials: true });
  }
}
