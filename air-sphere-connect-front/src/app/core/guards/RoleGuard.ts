import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import {UserService} from '../../shared/services/user-service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private userService: UserService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRoles = route.data['roles'] as string[];
    const profile = this.userService['_userProfileSubject'].value;

    // Vérifie soit le rôle global, soit les rôles dans le user complet
    const userRoles = (profile?.user?.role ?? [profile?.role]).filter((r): r is string => !!r);

    const hasRole = userRoles.some(r => expectedRoles.includes(r));


    if (hasRole) return true;

    this.router.navigate(['/home']);
    return false;
  }
}
