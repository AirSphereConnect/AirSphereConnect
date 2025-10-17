import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService, LoginRequest} from '../services/auth-feature.service';
import {Router} from '@angular/router';
import {InputField} from '../../../shared/components/input-field/input-field';
import {PasswordField} from '../../../shared/components/password-field/password-field';
import {UserService} from '../../../shared/services/UserService';

@Component({
  selector: 'app-login',
  templateUrl: './login.html',
  imports: [
    ReactiveFormsModule,
    InputField,
    PasswordField
  ],
  styleUrls: ['./login.scss']
})
export class Login implements OnInit {
  loginForm!: FormGroup;
  errorMessage: string | null = null;


  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private userService: UserService
  ) {
  }

  ngOnInit() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  get usernameControl()
    :
    FormControl {
    return this.loginForm.get('username') as FormControl;
  }

  get passwordControl()
    :
    FormControl {
    return this.loginForm.get('password') as FormControl;
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const credentials: LoginRequest = this.loginForm.value;
      this.authService.login(credentials).subscribe({
        next: (profile) => {
          this.userService.setUserProfile(profile);
          console.log('Connexion réussie, role:', profile.role);
          console.log('Connexion réussie, role:', profile.user);
          this.errorMessage = null;
          this.router.navigate([`/home`]);
        },
        error: (err) => {
          console.error('Erreur de connexion', err);
          this.errorMessage = "Nom d'utilisateur ou mot de passe incorrect.";
        }
      });

    }
  }
}
