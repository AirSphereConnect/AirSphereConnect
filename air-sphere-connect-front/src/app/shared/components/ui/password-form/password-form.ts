import {Component, EventEmitter, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {UserService} from '../../../services/user-service';
import {Router} from '@angular/router';
import {InputComponent} from '../input/input';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';

@Component({
  selector: 'app-password-form',
  imports: [
    ReactiveFormsModule,
    InputComponent,
    ButtonCloseModal
  ],
  templateUrl: './password-form.html',
  styleUrl: './password-form.scss'
})
export class PasswordForm implements OnChanges, OnInit {
  @Input() user: any = null;
  @Input() isOpen = signal(false);
  @Input() editingUserId!: number | null;
  @Input() initialPasswordData: any = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  passwordForm: FormGroup;

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(private readonly fb: FormBuilder, private readonly userService: UserService, private readonly router: Router) {
    this.passwordForm = this.fb.group({
      password: ['****']
    });
  }

  ngOnInit() {
    // 🔁 Synchronisation automatique avec le profil utilisateur
    this.userService.userProfile$.subscribe(profile => {
      if (profile?.user) {
        this.user = profile.user;
      }
    });
  }

  ngOnChanges() {
    if (this.initialPasswordData) {
      this.passwordForm.patchValue({
        password: ''
      });
    }
  }

  submit() {
    if (this.passwordForm.invalid) {
      return;
    }

    this.isLoading.set(true);
    const payload = { ...this.passwordForm.value };

    this.userService.editUser(this.editingUserId, payload).subscribe({
      next: (res) => {
        if (!res || Object.keys(res).length === 0) {
          this.userService.setUserProfile(null);
          this.userService.fetchUserProfile();
          this.router.navigate(['/home']);
        } else {
          this.userService.fetchUserProfile();
          this.updated.emit();
          this.closeModal.emit();
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        this.logError('Erreur lors de la mise à jour du mot de passe', err);
        this.isLoading.set(false);
        this.errorMessage.set('Erreur lors de la mise à jour.');
      }
    });
  }

  private logError(message: string, error?: unknown): void {
    // Only log errors in development mode
    if (typeof ngDevMode !== 'undefined' && ngDevMode) {
      console.error(message, error);
    }
  }

}
