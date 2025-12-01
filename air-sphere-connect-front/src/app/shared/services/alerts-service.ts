import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { ApiConfigService } from '../../core/services/api';
import { AddAlertPayload } from '../../core/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AlertsService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiConfigService);
  private readonly apiUrl = `${this.api.apiUrl}/alert/configurations`;


  editAlerts(payload: AddAlertPayload, editingAlertsId: number) {
    return this.http.put<void>(`${this.apiUrl}/${editingAlertsId}`, payload, { withCredentials: true });

  }

  addAlerts(payload: AddAlertPayload) {
    return this.http.post<void>(`${this.apiUrl}`, payload, { withCredentials: true });
  }

  deleteAlerts(editingAlertsId: number) {
    return this.http.delete<void>(`${this.apiUrl}/${editingAlertsId}`, { withCredentials: true });
  }
}
