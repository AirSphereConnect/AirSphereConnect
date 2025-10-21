import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule, FormsModule} from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../../shared/services/UserService';
import {CommonModule} from '@angular/common';
import {InputField} from '../../../shared/components/input-field/input-field';
import {PasswordField} from '../../../shared/components/password-field/password-field';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, InputField, PasswordField],
  selector: 'app-login',
  templateUrl: './login.html',
})
export class Login implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  get usernameControl(): FormControl {
    return this.loginForm.get('username') as FormControl;
  }

  get passwordControl(): FormControl {
    return this.loginForm.get('password') as FormControl;
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const credentials = this.loginForm.value;
      this.userService.login(credentials).subscribe({
        next: profile => {
          this.errorMessage = null;
          this.router.navigate(['/home']);
        },
        error: err => {
          this.errorMessage = "Nom d'utilisateur ou mot de passe incorrect.";
        }
      });
    }
  }
}
