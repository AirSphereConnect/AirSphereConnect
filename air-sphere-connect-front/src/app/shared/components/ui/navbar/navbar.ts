import {Component, Input, OnInit} from '@angular/core';
import {ButtonComponent} from '../button/button';
import {NavigationService} from '../../../services/navigation-service';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [
    ButtonComponent,
    NgClass
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {
  @Input() userRole!: string | null;

  constructor(private navigationService: NavigationService) {}

  logout() {
    this.navigationService.logout();
  }

  goToLogin() {
    this.navigationService.goToLogin();
  }

  goToHome() {
    this.navigationService.goToHome();
  }

  goToProfile() {
    this.navigationService.goToProfile();
  }

  goToRegister() {
    this.navigationService.goToRegister();
  }

  goToForum() {
    this.navigationService.goToForum();
  }

  goToDashBoard() {
    this.navigationService.goToDashBoard();
  }

  chooseOpacity(): string{
    if (this.userRole === 'GUEST') {
      return "opacity-50"
    } else {
      return "opacity-100"
    }
  }
}
