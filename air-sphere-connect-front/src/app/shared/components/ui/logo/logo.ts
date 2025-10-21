import { Component } from '@angular/core';
import {NgOptimizedImage} from "@angular/common";
import {ButtonComponent} from '../button/button';
import {NavigationService} from '../../../services/navigation-service';

@Component({
  selector: 'app-logo',
  imports: [
    NgOptimizedImage,
    ButtonComponent
  ],
  templateUrl: './logo.html',
  styleUrl: './logo.scss'
})
export class Logo {

  constructor(private navigationService: NavigationService) {}

  goToHome() {
    this.navigationService.goToHome();
  }
}
