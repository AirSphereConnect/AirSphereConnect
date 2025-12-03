import {Component, EventEmitter, Input, Output, signal} from '@angular/core';
import {ButtonCloseModal} from "../button-close-modal/button-close-modal";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Modal} from "../../modal/modal";
import {Button} from '../button/button';

@Component({
  selector: 'app-warning-message',
  imports: [
    ButtonCloseModal,
    FormsModule,
    ReactiveFormsModule,
    Button,
    Modal
  ],
  templateUrl: './warning-message.html',
  styleUrl: './warning-message.scss'
})
export class WarningMessage {
  @Input() isOpen = signal(false);
  @Output() close = new EventEmitter<void>();
  @Input() message!: any;
  @Output() confirm = new EventEmitter<void>();
}
