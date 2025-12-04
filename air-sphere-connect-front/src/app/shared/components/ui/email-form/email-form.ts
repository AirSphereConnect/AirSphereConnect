import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserService} from '../../../services/user-service';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {InputComponent} from '../input/input';
import {ErrorMessageService} from '../../../services/error-message-service';

@Component({
  selector: 'app-email-form',
  imports: [
    ReactiveFormsModule,
    ButtonCloseModal,
    InputComponent
  ],
  templateUrl: './email-form.html',
  styleUrl: './email-form.scss'
})
export class EmailForm implements OnChanges, OnInit {
  @Input() user: any = null;
  @Input() isOpen = signal(false);
  @Input() editingUserId!: number | null;
  @Input() initialEmailData: any = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  emailForm: FormGroup;
  private readonly errorMessageService = inject(ErrorMessageService);

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(private readonly fb: FormBuilder, private readonly userService: UserService) {
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
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
    if (this.initialEmailData) {
      this.emailForm.patchValue({
        email: this.initialEmailData.email,
      });
    }
  }

  submit() {
    const isNewEntry = !this.editingUserId;
    const emailValid = !this.emailForm;

    if (!this.emailForm.valid || !this.emailForm.dirty || (isNewEntry && !emailValid)) {
      this.errorMessageService.setMessage('Veuillez renseigner une adresse email.');
      return;
    }

    if (this.emailForm.invalid) return;
    this.isLoading.set(true);

    const payload: any = { ...this.emailForm.value };

    this.userService.editUser(this.editingUserId, payload).subscribe({
      next: () => {
        this.userService.fetchUserProfile();
        this.isLoading.set(false);
        this.updated.emit();
        this.closeModal.emit();
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessageService.setMessage('Erreur lors de la mise à jour.');
      }
    });
  }
}
