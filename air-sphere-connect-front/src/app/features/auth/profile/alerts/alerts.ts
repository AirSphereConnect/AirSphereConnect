import {Component, Input, signal} from '@angular/core';
import {User} from '../../../../core/models/user.model';
import {Button} from '../../../../shared/components/ui/button/button';
import {AlertsForm} from '../../../../shared/components/ui/alerts-form/alerts-form';

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

  isModalOpen = signal(false);
  editingAlertsId: number | null = null;
  initiaAlertlData: any = null;


  addAlerts() {
    this.editingAlertsId = null;
    this.initiaAlertlData = null;
    this.isModalOpen.set(true);
  }

  editAlerts(id: number) {
    const favorite = this.user?.alerts.find((f: any) => f.id === id);
    console.log(favorite);
    if (favorite) {
      this.editingAlertsId = id;
      this.initiaAlertlData = favorite;
      this.isModalOpen.set(true);
    }
  }

  onModalClose() {
    this.isModalOpen.set(false);
  }
}
