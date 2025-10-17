import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { UserService } from './shared/services/UserService';
import {Header} from './shared/components/layout/header/header';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Header],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App {
  userRole = signal<string | null>(null);

  constructor(private userService: UserService, private router: Router) {
    this.loadUserProfile();
    this.userService.userProfile$.subscribe(profile => {
      this.userRole.set(profile?.role ?? 'GUEST');
    });
  }

  loadUserProfile() {
    this.userService.fetchUserProfile();
  }
}
