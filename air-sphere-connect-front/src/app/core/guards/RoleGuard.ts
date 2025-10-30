import {inject, Injectable} from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import {UserService} from '../../shared/services/user-service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRoles = route.data['roles'] as string;
    const profile = this.userService.currentUserProfile;

    // Vérifie soit le rôle global, soit les rôles dans le user complet
    const userRole = profile?.user?.role ?? profile?.role;
    const hasRole = userRole ? expectedRoles.includes(userRole) : false;

    if (hasRole) return true;

    this.router.navigate(['/home']);
    return false;
  }
}
