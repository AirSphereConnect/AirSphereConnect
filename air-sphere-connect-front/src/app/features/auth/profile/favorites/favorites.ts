import {Component, Input, signal} from '@angular/core';
import {FavoritesForm} from '../../../../shared/components/ui/favorites-form/favorites-form';
import {Button} from '../../../../shared/components/ui/button/button';


@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.html',
  standalone: true,
  imports: [FavoritesForm, Button]
})
export class Favorites {
  @Input() user: any = null;
  isModalOpen = signal(false);
  editingFavoriteId: number | null = null;
  initialData: any = null;


  addFavorites() {
    this.editingFavoriteId = null;
    this.initialData = null;
    this.isModalOpen.set(true);
  }

  editFavorites(id: number) {
    const favorite = this.user?.favorites.find((f: any) => f.id === id);
    console.log(favorite);
    if (favorite) {
      this.editingFavoriteId = id;
      this.initialData = favorite;
      this.isModalOpen.set(true);
    }
  }

  onModalClose() {
    this.isModalOpen.set(false);
  }

}
