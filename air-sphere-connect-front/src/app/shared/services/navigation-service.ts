import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {UserService} from './user-service';

@Injectable({ providedIn: 'root' })
export class NavigationService {

  constructor(private userService: UserService, private router: Router) {}

  logout() {
    this.userService.logout().subscribe(() => {
      this.router.navigate(['/home']);
    });
  }

  goToLogin() {
    this.router.navigate(['/auth/login']);
  }

  goToHome() {
    this.router.navigate(['/home']);
  }

  goToProfile() {
    this.router.navigate(['/auth/profile']);
  }

  goToRegister() {
    this.router.navigate(['/auth/register']);
  }

  goToForum() {
    this.router.navigate(['/forum']);
  }

  goToDashBoard() {
    this.router.navigate(['/dashboard']);

  }
}
