import {Component, DestroyRef, inject, Input, signal} from '@angular/core';
import {Alerts as AlertModel, User} from '../../../../core/models/user.model';
import {Button} from '../../../../shared/components/ui/button/button';
import {AlertsForm} from '../../../../shared/components/ui/alerts-form/alerts-form';
import {UserService} from '../../../../shared/services/user-service';
import {AlertsService} from '../../../../shared/services/alerts-service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {WarningMessage} from '../../../../shared/components/ui/warning-message/warning-message';

@Component({
  selector: 'app-alerts',
  imports: [
    Button,
    AlertsForm,
    WarningMessage
  ],
  templateUrl: './alerts.html',
  styleUrl: './alerts.scss'
})
export class Alerts {
  @Input() user!: User | null;

  private readonly alertsService = inject(AlertsService);
  private readonly userService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);

  editingAlertsId: number | null = null;
  initialAlertData: any = null;
  alertToDeleteId: number | null = null;
  isModalOpen = signal(false);
  isWarningOpen = signal(false);
  warningMessage = signal<string | null>(null);

  addAlerts() {
    this.editingAlertsId = null;
    this.initialAlertData = null;
    this.isModalOpen.set(true);
  }

  editAlerts(id: number) {
    const alert = this.user?.alerts.find((f: AlertModel) => f.id === id);
    if (alert) {
      this.editingAlertsId = id;
      this.initialAlertData = alert;
      this.isModalOpen.set(true);
      this.userService.fetchUserProfile();
    }
  }

  deleteAlerts(id: number) {
    this.alertToDeleteId = id;
    this.warningMessage.set('Êtes-vous sûr de vouloir supprimer cette alerte ?');
    this.isWarningOpen.set(true);
  }

  confirmDelete() {
    if (this.alertToDeleteId !== null) {
      this.alertsService.deleteAlerts(this.alertToDeleteId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            this.userService.fetchUserProfile();
            this.alertToDeleteId = null;
            this.isWarningOpen.set(false);
            this.warningMessage.set(null);
          },
          error: () => {
            this.isWarningOpen.set(false);
            this.alertToDeleteId = null;
            this.warningMessage.set(null);
          }
        });
    }
  }


  onModalClose() {
    this.isModalOpen.set(false);
  }
}
