import { Router, RouterOutlet } from '@angular/router';
import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UserService } from './shared/services/UserService';
import {Component, signal, inject, OnInit} from '@angular/core';
import { ThemeService} from './core/services/theme';
import {Header} from './shared/components/layout/header/header';
import { Header } from './shared/components/layout/header/header';

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

  readonly isDarkTheme = this.themeService.isDarkMode;

  constructor(private userService: UserService) {
    this.userService.fetchUserProfile();
}


  ngOnInit() {
    this.themeService.watchSystemTheme();
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  loadUserProfile() {
    this.userService.fetchUserProfile();
  }
}
