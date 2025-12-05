import { Injectable, signal } from '@angular/core';

export type NotificationType = 'error' | 'success';
export interface Notification {
  type: NotificationType;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  notification = signal<Notification | null>(null);

  show(message: string, type: NotificationType, duration = 5000) {
    this.notification.set({ type, message });
    setTimeout(() => {
      this.notification.set(null);
    }, duration);
  }

  showError(message: string, duration = 5000) {
    this.show(message, 'error', duration);
  }

  showSuccess(message: string, duration = 3000) {
    this.show(message, 'success', duration);
  }

  clear() {
    this.notification.set(null);
  }
}
