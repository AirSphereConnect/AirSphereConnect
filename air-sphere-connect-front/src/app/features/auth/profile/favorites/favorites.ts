import { Component, Input, OnInit, signal } from '@angular/core';
import { FavoritesForm } from '../../../../shared/components/ui/favorites-form/favorites-form';
import { Button } from '../../../../shared/components/ui/button/button';
import { UserService } from '../../../../shared/services/user-service';
import { FavoritesService } from '../../../../shared/services/favorites-service';

@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.html',
  standalone: true,
  imports: [FavoritesForm, Button],
})
export class Favorites implements OnInit {
  @Input() user: any = null;

  isModalOpen = signal(false);
  editingFavoriteId: number | null = null;
  initialFavoriteData: any = null;

  constructor(
    private favoritesService: FavoritesService,
    private userService: UserService
  ) {}

  ngOnInit() {
    // 🔁 Synchronisation automatique avec le profil utilisateur
    this.userService.userProfile$.subscribe(profile => {
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

  /** 🗑️ Suppression d’un favori */
  deleteFavorites(id: number) {
    const favorite = this.user?.favorites.find((f: any) => f.id === id);
    if (favorite && confirm('Êtes-vous sûr de vouloir supprimer ce favori ?')) {
      this.favoritesService.deleteFavorites(id).subscribe({
        next: () => {
          // 🔁 rafraîchit le profil complet
          this.userService.fetchUserProfile();
          console.log(`Favori ${id} supprimé avec succès`);
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du favori :', err);
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
