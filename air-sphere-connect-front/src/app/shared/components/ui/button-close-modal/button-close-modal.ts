import { Component, EventEmitter, Output } from '@angular/core';
import {Button} from '../button/button';

@Component({
  selector: 'app-button-close-modal',
  templateUrl: './button-close-modal.html',
  imports: [
    Button
  ],
  styleUrls: ['./button-close-modal.scss']
})
export class ButtonCloseModal {
  @Output() close = new EventEmitter<void>();

  onClick() {
    this.close.emit();
  }
}
