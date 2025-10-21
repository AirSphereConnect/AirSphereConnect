import { Component, Input } from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatError, MatFormField} from '@angular/material/input';
import {MatLabel} from '@angular/material/input';
import {MatInput} from '@angular/material/input';

@Component({
  selector: 'app-input-field',
  templateUrl: './input-field.html',
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatError,
  ],
  styleUrl: './input-field.scss'
})
export class InputField {
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
