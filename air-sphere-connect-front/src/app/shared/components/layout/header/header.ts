import { Component, Input } from '@angular/core';

import { Router } from '@angular/router';
import {UserService} from '../../../services/UserService';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  styleUrls: ['./header.scss']
})
export class Header {
  @Input() userRole: string | null = null;

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
}
