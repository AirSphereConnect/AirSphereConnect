import {Component, Input} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatError, MatFormField, MatLabel} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {MatIconButton} from '@angular/material/button';
import {MatInput} from '@angular/material/input';

@Component({
  selector: 'app-password-field',
  imports: [
    MatFormField,
    ReactiveFormsModule,
    MatIcon,
    MatError,
    MatIconButton,
    MatInput,
    MatLabel
  ],
  templateUrl: './password-field.html',
  styleUrl: './password-field.scss'
})
export class PasswordField {
@Input() label!: string;
@Input() control!: FormControl;
@Input() placeholder: string = '';

showPassword = false;

toggleVisibility() {
  this.showPassword = !this.showPassword;
}

get inputType() {
  return this.showPassword ? 'text' : 'password';
}

get isInvalid() {
  return this.control && this.control.invalid && (this.control.dirty || this.control.touched);
}
}
