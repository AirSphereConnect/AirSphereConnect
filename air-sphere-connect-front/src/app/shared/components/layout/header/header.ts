import {Component, inject, Input} from '@angular/core';
import { ThemeService} from '../../../../core/services/theme';


import { Router } from '@angular/router';
import {UserService} from '../../../services/UserService';
import {Button} from '../../ui/button/button';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  imports: [
    Button
  ],
  styleUrls: ['./header.scss']
})
export class Header {
  @Input() userRole: string | null = null;
  readonly themeService = inject(ThemeService);
  constructor(private userService: UserService, private router: Router) {}

  logout() {
    this.userService.logout();
    this.router.navigate(['/home']);
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

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
