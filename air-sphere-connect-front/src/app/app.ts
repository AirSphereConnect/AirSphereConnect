import {Component, signal, inject, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ThemeService} from './core/services/theme';
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
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
