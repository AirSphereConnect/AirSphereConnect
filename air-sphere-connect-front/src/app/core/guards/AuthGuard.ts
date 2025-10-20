import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UserService} from '../../shared/services/UserService';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private userService: UserService, private router: Router) {}

  canActivate(): boolean {
    const user = this.userService['_userProfileSubject'].value;
    if (user && user.role !== 'GUEST') {
      return true; // accès autorisé
    } else {
      this.router.navigate(['/auth/login']); // redirection
      return false;
    }
  }
}
