import {inject, Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {UserService} from './user-service';

@Injectable({providedIn: 'root'})
export class NavigationService {

  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  logout() {
    this.userService.logout().subscribe(() => {
      this.router.navigate(['/home']);
    });
  }
}
