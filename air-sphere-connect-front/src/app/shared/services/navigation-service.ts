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
}
