import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {NavigationService} from '../../../services/navigation-service';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {Button} from '../button/button';
import {IconComponent} from '../icon/icon';
import {ThemeService} from '../../../../core/services/theme';

@Component({
  selector: 'app-navbar',
  imports: [
    RouterLink,
    RouterLinkActive,
    Button,
    IconComponent
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {
  @Input() userRole!: string | null;
  activeTab: string = 'home';
  @Output() linkClicked = new EventEmitter<void>();

  private readonly navigationService = inject(NavigationService);

  setActive(tab: string) {
    this.activeTab = tab;
  }

  logout() {
    this.setActive('home');
    this.navigationService.logout();
  }

  readonly themeService = inject(ThemeService);
  readonly isDarkTheme = this.themeService.isDarkMode;

  toggleTheme() {
    this.themeService.toggleTheme();
  }

  onLinkClick() {
    this.linkClicked.emit();
  }
}
