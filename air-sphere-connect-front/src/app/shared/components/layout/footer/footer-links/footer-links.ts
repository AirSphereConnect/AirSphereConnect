import {Component, Input} from '@angular/core';
import {NavigationService} from '../../../../services/navigation-service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-footer-links',
  imports: [
    RouterLink
  ],
  templateUrl: './footer-links.html',
  styleUrl: './footer-links.scss'
})
export class FooterLinks {
  @Input() userRole!: string | null;

  constructor(private navigationService: NavigationService) {}

  logout() {
    this.navigationService.logout();
  }
}
