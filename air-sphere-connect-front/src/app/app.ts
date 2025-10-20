import { Router, RouterOutlet } from '@angular/router';
import { UserService } from './shared/services/UserService';
import {Component, signal, inject, OnInit} from '@angular/core';
import {Header} from './shared/components/layout/header/header';
import {ThemeService} from './core/services/theme';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})

export class App implements OnInit {
  protected readonly title = signal('AirSphereConnect');
  private readonly themeService = inject(ThemeService);
  userRole = signal<string | null>(null);

  constructor(private userService: UserService, private router: Router) {
    this.loadUserProfile();
    this.userService.userProfile$.subscribe(profile => {
      this.userRole.set(profile?.role ?? 'GUEST');
    });
  }

  ngOnInit() {
    this.themeService.watchSystemTheme();
  }


  loadUserProfile() {
    this.userService.fetchUserProfile();
  }
}
