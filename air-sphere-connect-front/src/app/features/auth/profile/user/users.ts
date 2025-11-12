import {Component, Input, signal} from '@angular/core';
import { UserService } from '../../../../shared/services/user-service';
import { User } from '../../../../core/models/user.model';
import { UserForm } from '../../../../shared/components/ui/user-form/user-form';
import {AddressForm} from '../../../../shared/components/ui/adress-form/address-form';
import {EmailForm} from '../../../../shared/components/ui/email-form/email-form';
import {PasswordForm} from '../../../../shared/components/ui/password-form/password-form';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [UserForm, AddressForm, EmailForm, PasswordForm],
  templateUrl: './user.html',
  styleUrls: ['./user.scss']
})
export class UserDashboard {
  @Input() user: User | null = null;

  // Modales séparées
  isUserModalOpen = signal(false);
  isEmailModalOpen = signal(false);
  isPasswordModalOpen = signal(false);
  isAddressModalOpen = signal(false);

  // Données en cours d'édition
  editingUserId: number | null = null;
  initialUserData: any = null;
  initialEmailData: any = null;
  initialPasswordData: any = null;
  initialAddressData: any = null;

  constructor(protected userService: UserService) {
    // Suivi automatique du profil
    this.userService.userProfile$.subscribe(profile => {
      if (profile) {
        this.user = profile.user;
      }
    });
  }

  /** ✏️ Ouvre la modale utilisateur */
  editUser() {
    if (!this.user) return;
    this.editingUserId = this.user.id;
    this.initialUserData = this.user;
    this.isUserModalOpen.set(true);
  }
  /** ✏️ Ouvre la modale utilisateur */
  editEmail() {
    if (!this.user) return;
    this.editingUserId = this.user.id;
    this.initialEmailData = this.user;
    this.isEmailModalOpen.set(true);
  }
  /** ✏️ Ouvre la modale utilisateur */
  editPassword() {
    if (!this.user) return;
    this.editingUserId = this.user.id;
    this.initialPasswordData = this.user;
    this.isPasswordModalOpen.set(true);
  }

  /** ✏️ Ouvre la modale adresse */
  editAddress() {
    if (!this.user || !this.user.address) return;
    this.editingUserId = this.user.id;
    this.initialAddressData = this.user.address;
    this.isAddressModalOpen.set(true);
  }

  /** 🔒 Ferme la modale user */
  onUserModalClose() {
    this.isUserModalOpen.set(false);
  }

  /** 🔒 Ferme la modale user */
  onEmailModalClose() {
    this.isEmailModalOpen.set(false);
  }

  /** 🔒 Ferme la modale user */
  onPasswordModalClose() {
    this.isPasswordModalOpen.set(false);
  }

  /** 🔒 Ferme la modale adresse */
  onAddressModalClose() {
    this.isAddressModalOpen.set(false);
  }
}
