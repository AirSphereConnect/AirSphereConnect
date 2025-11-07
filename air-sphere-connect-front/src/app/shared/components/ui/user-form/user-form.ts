import {Component, EventEmitter, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {UserService} from '../../../services/user-service';
import {Router} from '@angular/router';
import {Button} from '../button/button';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [ReactiveFormsModule, Button],
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
    if (this.userForm.invalid) {
      console.log("[UserForm] Formulaire invalide");
      return;
    }

    this.isLoading.set(true);
    const payload = { ...this.userForm.value };

    console.log("[UserForm] Soumission des données:", payload);

    this.userService.editUser(this.editingUserId, payload).subscribe({
      next: (res) => {
        console.log("[UserForm] Réponse backend:", res);

        if (!res || Object.keys(res).length === 0) {
          console.log("[UserForm] Session invalidée côté backend, déconnexion forcée");

          this.userService.setUserProfile(null);
          this.userService.fetchUserProfile();
          this.router.navigate(['/home']);
        } else {
          console.log("[UserForm] Mise à jour normale, rafraîchissement du profil");

          this.userService.fetchUserProfile();
          this.updated.emit();
          this.close.emit();
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error("[UserForm] Erreur lors de la mise à jour:", err);
        this.isLoading.set(false);
        this.errorMessage.set('Erreur lors de la mise à jour.');
      }
    });
  }


}
