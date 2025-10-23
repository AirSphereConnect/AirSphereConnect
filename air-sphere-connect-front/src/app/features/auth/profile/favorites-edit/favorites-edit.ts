import { Component } from '@angular/core';
import {FormGroup} from '@angular/forms';
import {FavoritesService} from '../../../../shared/services/favorites-service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-favorites-edit',
  imports: [],
  templateUrl: './favorites-edit.html',
  styleUrl: './favorites-edit.scss'
})
export class FavoritesEdit {
  addFavoritesForm!: FormGroup;
  errorMessage: string | null = null;
  private id: number = 1;

  constructor(private favoritesService: FavoritesService, private router: Router) { }

  editFavorite(){
    if (this.addFavoritesForm.valid) {
      const credentials = this.addFavoritesForm.value;
      this.favoritesService.editFavorites(credentials, this.id).subscribe({
        next: profile => {
          this.errorMessage = null;
          this.router.navigate(['/auth/profile']);
        },
        error: err => {
          this.errorMessage = "Nom d'utilisateur ou mot de passe incorrect.";
        }
      });
    }
  }
}
