import {inject, Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UserService} from '../../shared/services/user-service';

@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {

  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  canActivate(): boolean {
    const user = this.userService.currentUserProfile;

    if (user && user.role !== 'GUEST') {
      return true;
    }
    this.router.navigate(['/auth/login']); // redirection
    return false;
  }
}
