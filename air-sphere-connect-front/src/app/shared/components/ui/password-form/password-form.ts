import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserService} from '../../../services/user-service';
import {Router} from '@angular/router';
import {Button} from '../button/button';
import {InputComponent} from '../input/input';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {ErrorMessageService} from '../../../services/error-message-service';

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
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  passwordForm: FormGroup;
  private readonly errorMessageService = inject(ErrorMessageService);

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(private fb: FormBuilder, private userService: UserService, private router: Router) {
    this.passwordForm = this.fb.group({
      password: ['****']
    });
  }

  ngOnInit() {
    // 🔁 Synchronisation automatique avec le profil utilisateur
    this.userService.userProfile$.subscribe(profile => {
      if (profile && profile.user) {
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
    const isNewEntry = !this.editingUserId;
    const passwordFormValid = !this.passwordForm;

    if (!this.passwordForm.valid || !this.passwordForm.dirty || (isNewEntry && !passwordFormValid)) {
      this.errorMessageService.setMessage('Veuillez renseigner un nouveau mot de passe.');
      return;
    }

    if (this.passwordForm.invalid) return;
    this.isLoading.set(true);

    const payload = { ...this.passwordForm.value };


    this.userService.editUser(this.editingUserId, payload).subscribe({
      next: (res) => {

        if (!res || Object.keys(res).length === 0) {
          this.errorMessageService.setMessage("Session invalidée côté backend, déconnexion forcée");
          this.userService.setUserProfile(null);
          this.userService.fetchUserProfile();
          this.router.navigate(['/home']);
        } else {
          this.errorMessageService.setMessage("Mise à jour normale, rafraîchissement du profil");
          this.userService.fetchUserProfile();
          this.updated.emit();
          this.close.emit();
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessageService.setMessage('Erreur lors de la mise à jour.');
      }
    });
  }

}
