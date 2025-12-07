import {Component, OnInit, signal, computed, inject, DestroyRef} from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import { UserService } from '../../../shared/services/user-service';
import { CommonModule } from '@angular/common';
import {InputComponent} from '../../../shared/components/ui/input/input';
import {Button} from '../../../shared/components/ui/button/button';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {NotificationService} from '../../../shared/services/notification-service';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    InputComponent,
    Button,
    RouterLink
  ],
  selector: 'app-login',
  templateUrl: './login.html',
})
export class Login implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  private readonly destroyRef = inject(DestroyRef);
  private readonly notificationService = inject(NotificationService);

  loginForm!: FormGroup;

  // 🎯 Signals
  isLoading = signal<boolean>(false);
  passwordType = signal<'password' | 'text'>('password');

  // 🎯 Computed signals
  isFormValid = computed(() => this.loginForm?.valid ?? false);
  canSubmit = computed(() => this.isFormValid() && !this.isLoading());
  passwordIcon = computed(() => this.passwordType() === 'password' ? 'eye' : 'eyeSlash');


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

  onSubmit() {
    if (this.loginForm.valid && !this.isLoading()) {
      this.isLoading.set(true);

      const credentials = this.loginForm.value;

      this.userService.login(credentials)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            this.notificationService.showSuccess('Connexion réussie')
            this.isLoading.set(false);
            this.router.navigate(['/home']).then();
          },
        error: err => {
          this.isLoading.set(false);
          if (err.status === 0) {
            this.notificationService.showError('Impossible de contacter le serveur.');
          } else if (err.status === 401) {
            this.notificationService.showError('Nom d\'utilisateur ou mot de passe incorrect.');
          } else if (err.status === 403) {
            this.notificationService.showError('Accès refusé.');
          } else if (err.status === 404) {
            this.notificationService.showError('Service non disponible.');
          } else if (err.status === 500) {
            this.notificationService.showError('Erreur serveur. Veuillez réessayer plus tard.');
          } else {
            this.notificationService.showError('Une erreur est survenue lors de la connexion.');
          }
        }
      });
    }
  }

  togglePasswordVisibility() {
    this.passwordType.set(this.passwordType() === 'password' ? 'text' : 'password');
  }
}
