import { Component, Input, signal, effect } from '@angular/core';
import { User } from '../../../../core/models/user.model';
import { Button } from '../../../../shared/components/ui/button/button';
import { UserService } from '../../../../shared/services/user-service';
import { UserForm } from '../../../../shared/components/ui/user-form/user-form';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [Button, UserForm],
  templateUrl: './user.html',
  styleUrls: ['./user.scss']
})
export class UserDashboard {
  @Input() user!: User | null;

  editingUserId: number | null = null;
  initialUserData: any = null;
  isModalOpen = signal(false);

  constructor(private userService: UserService, private router: Router) {
    // 👀 Met à jour automatiquement "user" quand le profil change
    effect(() => {
      const currentProfile = this.userService._userProfileSubject.value;
      if (currentProfile) {
        this.user = currentProfile.user;
      }
    });
  }

  /** ✏️ Ouvre la modale d’édition */
  editUser(id: number) {
    console.log('editUser', id);
    if (this.user && this.user.id === id) {
      this.editingUserId = id;
      this.initialUserData = { ...this.user };
      this.isModalOpen.set(true);
      console.log('isModalOpen:', this.isModalOpen());
    }
  }

  /** 🗑️ Supprime le compte utilisateur */
  deleteUser() {
    if (confirm('Voulez-vous vraiment supprimer votre compte ?')) {
      this.userService.deleteUser().subscribe({
        next: () => {
          // ✅ Déconnexion automatique après suppression
          this.userService.logout().subscribe({
            next: () => {
              alert('Votre compte a été supprimé et vous avez été déconnecté.');
              this.router.navigate(['/']); // 🔁 redirige vers la page d’accueil
            },
            error: (err) => {
              console.error('Erreur lors de la déconnexion :', err);
              this.router.navigate(['/']);
            }
          });
        },
        error: (err) => {
          console.error('Erreur lors de la suppression:', err);
          alert('Erreur lors de la suppression du compte.');
        }
      });
    }
  }

  /** ✅ Fermeture de la modale et rafraîchissement de l’utilisateur */
  onModalClose() {
    console.log('Fermeture modale');
    this.isModalOpen.set(false);
    // 🔁 Rafraîchir le profil pour afficher les infos à jour
    this.userService.fetchUserProfile();
  }
}
