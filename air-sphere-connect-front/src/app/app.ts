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

export class App implements OnInit {
  protected readonly title = signal('AirSphereConnect');

  private readonly themeService = inject(ThemeService);

  readonly isDarkTheme = this.themeService.isDarkMode;

  ngOnInit() {
    this.themeService.watchSystemTheme();
  constructor(private userService: UserService) {
    this.userService.fetchUserProfile();

    this.userService.userProfile$.subscribe(profile => {
      this.userRole.set(profile?.role ?? 'GUEST');
    });
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
