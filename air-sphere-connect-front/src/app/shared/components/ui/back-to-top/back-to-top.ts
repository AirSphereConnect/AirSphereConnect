import {Component, EventEmitter, Output} from '@angular/core';
import {Button} from '../button/button';

@Component({
  selector: 'app-back-to-top',
  imports: [
    Button
  ],
  templateUrl: './back-to-top.html',
  styleUrl: './back-to-top.scss'
})
export class BackToTop {
  onClick() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
