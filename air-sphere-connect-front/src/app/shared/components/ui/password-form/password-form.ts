import {Component, EventEmitter, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserService} from '../../../services/user-service';
import {Router} from '@angular/router';
import {Button} from '../button/button';
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
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  passwordForm: FormGroup;

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
    if (this.passwordForm.invalid) {
      console.log("[PasswordForm] Formulaire invalide");
      return;
    }

    this.isLoading.set(true);
    const payload = { ...this.passwordForm.value };

    console.log("[PasswordForm] Soumission des données:", payload);

    this.userService.editUser(this.editingUserId, payload).subscribe({
      next: (res) => {
        console.log("[PasswordForm] Réponse backend:", res);

        if (!res || Object.keys(res).length === 0) {
          console.log("[PasswordForm] Session invalidée côté backend, déconnexion forcée");

          this.userService.setUserProfile(null);
          this.userService.fetchUserProfile();
          this.router.navigate(['/home']);
        } else {
          console.log("[PasswordForm] Mise à jour normale, rafraîchissement du profil");

          this.userService.fetchUserProfile();
          this.updated.emit();
          this.close.emit();
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error("[PasswordForm] Erreur lors de la mise à jour:", err);
        this.isLoading.set(false);
        this.errorMessage.set('Erreur lors de la mise à jour.');
      }
    });
  }

}
