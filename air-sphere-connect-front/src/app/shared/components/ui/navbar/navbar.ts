import {Component, inject, Input} from '@angular/core';
import {NavigationService} from '../../../services/navigation-service';
import {RouterLink, RouterLinkActive} from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {
  @Input() userRole!: string | null;
  activeTab: string = 'home';

  private readonly navigationService = inject(NavigationService);

  setActive(tab: string) {
    this.activeTab = tab;
  }

  logout() {
    this.setActive('home');
    this.navigationService.logout();
  }
}
