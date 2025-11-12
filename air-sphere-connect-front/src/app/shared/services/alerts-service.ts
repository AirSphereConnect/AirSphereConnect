import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { ApiConfigService } from '../../core/services/api';

@Injectable({
  providedIn: 'root'
})
export class AlertsService {
  private http = inject(HttpClient);
  private api = inject(ApiConfigService);
  private readonly apiUrl = `${this.api.apiUrl}/alert/configurations`;


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
