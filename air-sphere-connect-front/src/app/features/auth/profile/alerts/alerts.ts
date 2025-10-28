import {Component, Input, signal} from '@angular/core';
import {User} from '../../../../core/models/user.model';
import {Button} from '../../../../shared/components/ui/button/button';
import {AlertsForm} from '../../../../shared/components/ui/alerts-form/alerts-form';
import {UserService} from '../../../../shared/services/user-service';
import {AlertsService} from '../../../../shared/services/alerts-service';

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


  constructor(public alertsService: AlertsService, private userService: UserService) {
  }

  isModalOpen = signal(false);
  editingAlertsId: number | null = null;
  initiaAlertlData: any = null;


  addAlerts() {
    this.editingAlertsId = null;
    this.initiaAlertlData = null;
    this.isModalOpen.set(true);
  }

  editAlerts(id: number) {
    const alert = this.user?.alerts.find((f: any) => f.id === id);
    if (alert) {
      this.editingAlertsId = id;
      this.initiaAlertlData = alert;
      this.isModalOpen.set(true);
      this.userService.fetchUserProfile();
    }
  }

  deleteAlerts(id: number) {
    const alert = this.user?.alerts.find((a: any) => a.id === id);
    if (alert && confirm('Êtes-vous sûr de vouloir supprimer cette alerte ?')) {
      this.alertsService.deleteAlerts(id).subscribe({
        next: () => {
          this.userService.fetchUserProfile(); // 🔁 refresh user alerts
          console.log(`Alerte ${id} supprimée avec succès`);
        },
        error: () => {
          console.error("Erreur lors de la suppression de l'alerte");
        }
      });
    }
  }


  onModalClose() {
    this.isModalOpen.set(false);
  }
}
