import { Component, Input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input',
  templateUrl: './input.html',
  imports: [
    ReactiveFormsModule,
  ],
  styleUrl: './input.scss'
})
export class InputComponent {
  @Input()
  label!: string;

  @Input()
  type: string = 'text';

  @Input()
  control!: FormControl;

  @Input()
  placeholder!: string;

  get isInvalid() {
    return this.control && this.control.invalid && (this.control.dirty || this.control.touched);
  }
}
