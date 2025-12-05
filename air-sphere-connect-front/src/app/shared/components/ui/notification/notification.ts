// notification.ts
import { Component, inject } from '@angular/core';
import { NotificationService } from '../../../services/notification-service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.html',
  styleUrls: ['./notification.scss'],
  standalone: true
})
export class Notification {
  private readonly notificationService = inject(NotificationService);
  notification = this.notificationService.notification;
}
