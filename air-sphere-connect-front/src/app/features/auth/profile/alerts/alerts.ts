import {Component, DestroyRef, inject, Input, signal} from '@angular/core';
import {Alerts as AlertModel, User} from '../../../../core/models/user.model';
import {Button} from '../../../../shared/components/ui/button/button';
import {AlertsForm} from '../../../../shared/components/ui/alerts-form/alerts-form';
import {UserService} from '../../../../shared/services/user-service';
import {AlertsService} from '../../../../shared/services/alerts-service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-alerts',
  imports: [
    Button,
    AlertsForm
  ],
  templateUrl: './alerts.html',
  styleUrl: './alerts.scss'
})
export class Alerts {
  @Input() user!: User | null;

  private readonly alertsService = inject(AlertsService);
  private readonly userService = inject(UserService);

  private readonly destroyRef = inject(DestroyRef);

  isModalOpen = signal(false);
  editingAlertsId: number | null = null;
  initialAlertData: any = null;


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
    const alert = this.user?.alerts.find((a: AlertModel) => a.id === id);
    if (alert && confirm('Êtes-vous sûr de vouloir supprimer cette alerte ?')) {
      this.alertsService.deleteAlerts(id)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
        next: () => {
          this.userService.fetchUserProfile(); // 🔁 refresh user alerts
        },
        error: (err) => {
          this.logError("Erreur lors de la suppression de l'alerte", err);
        }
      });
    }
  }

  private logError(message: string, error?: unknown): void {
    // Only log errors in development mode
    if (typeof ngDevMode !== 'undefined' && ngDevMode) {
      console.error(message, error);
    }
  }

  onModalClose() {
    this.isModalOpen.set(false);
  }
}
