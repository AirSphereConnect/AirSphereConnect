import { Component, Input } from '@angular/core';
import { User } from '../../../../core/models/user.model';
import {ButtonComponent} from '../../../../shared/components/ui/button/button';
import {Router} from '@angular/router';

@Component({
  selector: 'app-favorite-dashboard',
  standalone: true,
  templateUrl: './favorites.html',
  imports: [
    ButtonComponent
  ],
  styleUrls: ['./favorites.scss']
})
export class Favorites {
  @Input() user!: User | null;

  constructor(private router: Router) {}

  addFavorites() {
    this.router.navigate(['/auth/profile/favorites/add']).then(r => r);
  }

  editFavorites() {
    this.router.navigate(['/auth/profile/favorites/edit']).then(r => r);
  }
}

