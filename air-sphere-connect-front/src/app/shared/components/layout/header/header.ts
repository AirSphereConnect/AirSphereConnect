import {Component, inject, Input, OnInit, signal} from '@angular/core';
import { Navbar } from '../../ui/navbar/navbar';
import {Logo} from '../../ui/logo/logo';
import {ThemeService} from '../../../../core/services/theme';
import {Button} from '../../ui/button/button';
import {IconComponent} from '../../ui/icon/icon';

@Component({
  selector: 'app-header',
  standalone: true,
  templateUrl: './header.html',
  styleUrls: ['./header.scss'],
  imports: [
    Navbar,
    Logo,
    Button,
    IconComponent,
  ]
})
export class Header {
  @Input() userRole: string | null = null;

  readonly themeService = inject(ThemeService);
  readonly isDarkTheme = this.themeService.isDarkMode;

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
