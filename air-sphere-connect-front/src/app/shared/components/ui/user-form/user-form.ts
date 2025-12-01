import {Component, EventEmitter, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {UserService} from '../../../services/user-service';
import {Router} from '@angular/router';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {InputComponent} from '../input/input';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonCloseModal, InputComponent],
  templateUrl: './user-form.html',
})
export class UserForm implements OnChanges, OnInit {
  @Input() user: any = null;
  @Input() isOpen = signal(false);
  @Input() editingUserId!: number | null;
  @Input() initialUserData: any = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  userForm: FormGroup;

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(private readonly fb: FormBuilder, private readonly userService: UserService, private readonly router: Router) {
    this.userForm = this.fb.group({
      username: ['', Validators.required]
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
    if (this.initialUserData) {
      this.userForm.patchValue({
        username: this.initialUserData.username
      });
    }
  }

  submit() {
    if (this.userForm.invalid) {
      return;
    }

    this.isLoading.set(true);
    const payload = { ...this.userForm.value };

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
        this.logError('Erreur lors de la mise à jour utilisateur', err);
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
