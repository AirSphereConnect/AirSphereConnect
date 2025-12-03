import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {UserService} from '../../../services/user-service';
import {Router} from '@angular/router';
import {Button} from '../button/button';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {InputComponent} from '../input/input';
import {ErrorMessageService} from '../../../services/error-message-service';

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
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  userForm: FormGroup;
  private readonly errorMessageService = inject(ErrorMessageService);

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(private fb: FormBuilder, private userService: UserService, private router: Router) {
    this.userForm = this.fb.group({
      username: ['', Validators.required]
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
    if (this.initialUserData) {
      this.userForm.patchValue({
        username: this.initialUserData.username
      });
    }
  }

  submit() {
    const isNewEntry = !this.editingUserId;
    const userValid = !this.userForm;

    if (!this.userForm.valid || !this.userForm.dirty || (isNewEntry && !userValid)) {
      this.errorMessageService.setMessage('Veuillez renseigner une adresse email.');
      return;
    }

    if (this.userForm.invalid) return;
    this.isLoading.set(true);

    const payload = { ...this.userForm.value };


    this.userService.editUser(this.editingUserId, payload).subscribe({
      next: (res) => {

        if (!res || Object.keys(res).length === 0) {
          this.errorMessageService.setMessage("Erreur lors de la mise à jour.");
          this.userService.setUserProfile(null);
          this.userService.fetchUserProfile();
          this.router.navigate(['/home']);
        } else {
          this.errorMessageService.setMessage("Erreur lors de la mise à jour.");
          this.userService.fetchUserProfile();
          this.updated.emit();
          this.close.emit();
        }
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessageService.setMessage("Erreur lors de la mise à jour.");
      }
    });
  }


}
