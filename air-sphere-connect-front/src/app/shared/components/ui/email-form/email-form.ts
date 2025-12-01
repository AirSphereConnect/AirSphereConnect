import {Component, EventEmitter, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserService} from '../../../services/user-service';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {InputComponent} from '../input/input';

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
        this.errorMessage.set('Erreur lors de la mise à jour.');
      }
    });
  }
}
