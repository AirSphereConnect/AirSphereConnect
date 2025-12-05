import {Component, EventEmitter, Input, inject, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {UserService} from '../../../services/user-service';
import {Router} from '@angular/router';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {InputComponent} from '../input/input';
import {NotificationService} from '../../../services/notification-service';
import {Button} from '../button/button';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonCloseModal, InputComponent, Button],
  templateUrl: './user-form.html',
})
export class UserForm implements OnChanges, OnInit {
  @Input() user: any = null;
  @Input() isOpen = signal(false);
  @Input() editingUserId!: number | null;
  @Input() initialUserData: any = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  userForm!: FormGroup;
  private readonly notificationService = inject(NotificationService);
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  isLoading = signal(false);

  ngOnInit() {
    this.userForm = this.fb.group({
      username: ['', Validators.required]
    });

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
    const isNewEntry = !this.editingUserId;
    const userValid = !this.userForm;

    if (this.userForm.invalid) return;

    if (!this.userForm.valid || !this.userForm.dirty || (isNewEntry && !userValid)) {
      this.notificationService.showError('Veuillez renseigner une adresse email.');
      return;
    }

    this.isLoading.set(true);
    const payload = {...this.userForm.value};

    this.userService.editUser(this.editingUserId, payload).subscribe({
      next: (res) => {

        if (!res || Object.keys(res).length === 0) {
          this.notificationService.showError('Erreur lors de la mise à jour.');
          this.userService.setUserProfile(null);
          this.userService.fetchUserProfile();
          this.router.navigate(['/home']);
        } else {
          this.notificationService.showError('Erreur lors de la mise à jour.');
          this.userService.fetchUserProfile();
          this.updated.emit();
          this.closeModal.emit();
        }
        this.notificationService.showSuccess('Données utilisateur modifiées avec succès, Veuillez vous reconnecter')
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.notificationService.showError("Erreur lors de la mise à jour.");
      }
    });
  }

  private handleSuccess() {
    this.userForm.reset();
    this.userService.fetchUserProfile();
    this.submitSuccess.emit();
    this.onClose();
  }

  onClose() {
    this.userForm.reset({
      activeWeather: false,
      activeAirQuality: false,
      activePopulation: false,
      cityName: ''
    });
    this.isOpen.set(false);
    this.onClose();
  }
}
