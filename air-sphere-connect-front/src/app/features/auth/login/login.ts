import { Component, OnInit, signal, computed } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { UserService } from '../../../shared/services/user-service';
import { CommonModule } from '@angular/common';
import {InputComponent} from '../../../shared/components/ui/input/input';
import {Button} from '../../../shared/components/ui/button/button';
import {IconComponent} from '../../../shared/components/ui/icon/icon';
import {HeroIconName} from '../../../shared/icons/heroicons.registry';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    InputComponent,
    Button,
    RouterLink,
    IconComponent
  ],
  selector: 'app-login',
  templateUrl: './login.html',
})
export class Login implements OnInit {
  loginForm!: FormGroup;

  // 🎯 Signals
  errorMessage = signal<string | null>(null);
  isLoading = signal<boolean>(false);
  passwordVisible = signal(false);

  // 🎯 Computed signals
  isFormValid = computed(() => this.loginForm?.valid ?? false);
  canSubmit = computed(() => this.isFormValid() && !this.isLoading());

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  get usernameControl(): FormControl {
    return this.loginForm.get('username') as FormControl;
  }

  get passwordControl(): FormControl {
    return this.loginForm.get('password') as FormControl;
  }

  togglePasswordVisibility() {
    this.passwordVisible.set(!this.passwordVisible());
  }

  passwordIcon = computed<HeroIconName>(() => this.passwordVisible() ? 'eyeSlash' : 'eye');
  passwordType = computed(() => this.passwordVisible() ? 'text' : 'password');

  onSubmit() {
    if (this.loginForm.valid && !this.isLoading()) {
      this.isLoading.set(true);
      this.errorMessage.set(null);

      const credentials = this.loginForm.value;

      this.userService.login(credentials).subscribe({
        next: profile => {
          this.isLoading.set(false);
          this.errorMessage.set(null);
          this.router.navigate(['/home']).then();
        },
        error: err => {
          this.isLoading.set(false);
          if (err.status === 0) {
            this.errorMessage.set("Impossible de contacter le serveur.");
          } else if (err.status === 401) {
            this.errorMessage.set("Nom d'utilisateur ou mot de passe incorrect.");
          } else if (err.status === 403) {
            this.errorMessage.set("Accès refusé.");
          } else if (err.status === 404) {
            this.errorMessage.set("Service non disponible.");
          } else if (err.status === 500) {
            this.errorMessage.set("Erreur serveur. Veuillez réessayer plus tard.");
          } else {
            this.errorMessage.set("Une erreur est survenue lors de la connexion.");
          }
        }
      });
    }
  }

  clearError() {
    this.errorMessage.set(null);
  }
}
