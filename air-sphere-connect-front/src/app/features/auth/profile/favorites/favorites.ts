import {Component, DestroyRef, inject, Input, OnDestroy, OnInit, signal} from '@angular/core';
import {FavoritesForm} from '../../../../shared/components/ui/favorites-form/favorites-form';
import {Button} from '../../../../shared/components/ui/button/button';
import {UserService} from '../../../../shared/services/user-service';
import {FavoritesService} from '../../../../shared/services/favorites-service';
import {Subject, takeUntil} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {WarningMessage} from '../../../../shared/components/ui/warning-message/warning-message';

@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.html',
  standalone: true,
  imports: [FavoritesForm, Button, WarningMessage],
})
export class Favorites implements OnInit {
  @Input() user: any = null;

  private readonly favoritesService = inject(FavoritesService);
  private readonly userService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);

  editingFavoriteId: number | null = null;
  initialFavoriteData: any = null;
  favoritesToDeleteId: number | null = null;
  isModalOpen = signal(false);
  isWarningOpen = signal(false);
  warningMessage = signal<string | null>(null);


  ngOnInit() {
    // Synchronisation automatique avec le profil utilisateur
    this.userService.userProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(profile => {
        if (profile && profile.user) {
          this.user = profile.user;
        }
      });
  }


  /** ➕ Ajout d’un favori */
  addFavorites() {
    this.editingFavoriteId = null;
    this.initialFavoriteData = null;
    this.isModalOpen.set(true);
  }

  /** ✏️ Modification d’un favori existant */
  editFavorites(id: number) {
    const favorite = this.user?.favorites.find((f: any) => f.id === id);
    if (favorite) {
      this.editingFavoriteId = id;
      this.initialFavoriteData = favorite;
      this.isModalOpen.set(true);
    }
  }

  deleteFavorites(id: number) {
    this.favoritesToDeleteId = id;
    this.warningMessage.set('Êtes-vous sûr de vouloir supprimer ce favoris ?');
    this.isWarningOpen.set(true);
  }

  /** 🗑️ Suppression d’un favori */
  confirmDelete() {
    if (this.favoritesToDeleteId !== null) {
      this.favoritesService.deleteFavorites(this.favoritesToDeleteId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            this.userService.fetchUserProfile();
            console.log(`Favoris ${this.favoritesToDeleteId} supprimée avec succès`);
            this.favoritesToDeleteId = null;
            this.isWarningOpen.set(false);
            this.warningMessage.set(null);
          },
          error: () => {
            console.error("Erreur lors de la suppression du favoris");
            this.isWarningOpen.set(false);
            this.favoritesToDeleteId = null;
            this.warningMessage.set(null);
          }
        });
    }
  }

  /** 🔒 Ferme la modale */
  onModalClose() {
    this.isModalOpen.set(false);
    // 🔁 Rafraîchit aussi après fermeture de la modale
    this.userService.fetchUserProfile();
  }
}
