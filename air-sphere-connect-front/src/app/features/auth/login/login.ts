import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService, LoginRequest} from '../services/auth-feature.service';
import {InputField} from '../../../shared/components/input-field/input-field';
import {PasswordField} from '../../../shared/components/password-field/password-field';
import {MatButton} from '@angular/material/button';


@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    InputField,
    PasswordField,
    MatButton
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string | null = null;

  constructor(private fb: FormBuilder, private authService: AuthService) {}

  get usernameControl(): FormControl {
    return this.loginForm.get('username') as FormControl;
  }

  get passwordControl(): FormControl {
    return this.loginForm.get('password') as FormControl;
  }

  ngOnInit() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const credentials: LoginRequest = this.loginForm.value;
      this.authService.login(credentials).subscribe({
        next: (response) => {
          console.log('Connexion réussie, token:', response.token);
          // Stockage token + redirection par ex.
          this.errorMessage = null;
        },
        error: (err) => {
          this.errorMessage = 'Nom d’utilisateur ou mot de passe incorrect.';
          console.error('Erreur de connexion', err);
        }
      });
    }
  }
}
