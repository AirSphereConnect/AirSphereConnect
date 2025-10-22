import {Component, inject, Input, OnInit, signal} from '@angular/core';
import { Navbar } from '../../ui/navbar/navbar';
import { NgOptimizedImage } from '@angular/common';
import {Logo} from '../../ui/logo/logo';
import {ThemeService} from '../../../../core/services/theme';

@Component({
  selector: 'app-header',
  standalone: true, // ⚠️ important si tu veux importer des composants directement
  templateUrl: './header.html',
  styleUrls: ['./header.scss'],
  imports: [
    Navbar,
    Logo,
    NgOptimizedImage
  ]
})
export class Header implements OnInit {
  @Input() userRole: string | null = null;

  private readonly themeService = inject(ThemeService);

  readonly isDarkTheme = this.themeService.isDarkMode;

  ngOnInit() {
    this.themeService.watchSystemTheme();
  }

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
