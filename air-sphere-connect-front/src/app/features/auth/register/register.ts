import {
  Component,
  computed,
  DestroyRef,
  inject,
  OnInit,
  signal
} from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { UserService } from '../../../shared/services/user-service';
import { Router, RouterLink } from '@angular/router';
import { InputComponent } from '../../../shared/components/ui/input/input';
import { Button } from '../../../shared/components/ui/button/button';
import { IconComponent } from '../../../shared/components/ui/icon/icon';
import { HeroIcon } from '../../../shared/icons/heroicons.registry';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {citySearch} from '../../../shared/utils/city-search.util';
import {CityService} from '../../../core/services/city';
import {City} from '../../../core/models/city.model';
import {UserProfileResponse} from '../../../core/models/user.model';
import {NotificationService} from '../../../shared/services/notification-service';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    InputComponent,
    Button,
    RouterLink,
    IconComponent,
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
  standalone: true
})
export class Register implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly cityService = inject(CityService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly notificationService = inject(NotificationService);


  step = signal<number>(1);
  registerForm!: FormGroup;
  registerFirstForm!: FormGroup;

  cityQuery = signal<string>('');
  citySuggestions = signal<any[]>([]);
  cityIdSelected: number | null = null;

  isLoadingStep1 = signal<boolean>(false);
  isLoadingStep2 = signal<boolean>(false);
  passwordVisible = signal(false);

  canSubmitStep1 = signal<boolean>(false);
  canSubmitStep2 = signal<boolean>(false);

  // Effet Angular 20 pour recherche villes
  citySearchEffect = citySearch(this.cityService, this.cityQuery, this.citySuggestions);

  ngOnInit() {
    this.registerFirstForm = this.fb.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(25),
        this.validUsernameValidator
      ]],
      email: ['', [
        Validators.required,
        this.strictEmailValidator
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(25),
        this.strongPasswordValidator
      ]]
    });

    this.registerForm = this.fb.group({
      address: ['', Validators.required],
      cityName: ['', Validators.required],
      cityCode: ['', Validators.required]
    });

    this.registerFirstForm.statusChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.canSubmitStep1.set(this.registerFirstForm.valid);
      });
    this.registerForm.statusChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.canSubmitStep2.set(this.registerForm.valid));
    this.registerFirstForm.updateValueAndValidity()
  }

  get usernameControl() { return this.registerFirstForm.get('username') as FormControl; }
  get emailControl() { return this.registerFirstForm.get('email') as FormControl; }
  get passwordControl() { return this.registerFirstForm.get('password') as FormControl; }
  get addressControl() { return this.registerForm.get('address') as FormControl; }
  get cityNameControl() { return this.registerForm.get('cityName') as FormControl; }
  get cityCodeControl() { return this.registerForm.get('cityCode') as FormControl; }

  onCityInput(event: Event) {
    const target = event.target as HTMLInputElement;
    this.cityQuery.set(target.value);
  }

  selectCity(city: City) {
    this.cityNameControl.setValue(city.name);
    this.cityCodeControl.setValue(city.postalCode);
    this.cityIdSelected = city.id;
    this.citySuggestions.set([]);
  }

  togglePasswordVisibility() {
    this.passwordVisible.set(!this.passwordVisible());
  }

  passwordIcon = computed<HeroIcon>(() => this.passwordVisible() ? 'eyeSlash' : 'eye');
  passwordType = computed(() => this.passwordVisible() ? 'text' : 'password');

  onFirstSubmit() {
    if (this.registerFirstForm.invalid || this.isLoadingStep1()) return;

    this.isLoadingStep1.set(true);

    const { username, email } = this.registerFirstForm.value;

    this.userService.checkAvailability(username, email)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.isLoadingStep1.set(false);
          if (res.usernameTaken) {
            this.notificationService.showError('Nom d\'utilisateur déjà pris.');
          } else if (res.emailTaken) {
            this.notificationService.showError('Adresse email déjà utilisée.');
          } else {
            this.step.set(2);
          }
          this.notificationService.showSuccess('Données validées.')
        },
        error: (err) => {
          this.isLoadingStep1.set(false);
          if (err.status === 0) {
            this.notificationService.showError('Impossible de contacter le serveur.');
          } else if (err.status === 404) {
            this.notificationService.showError('Service non disponible.');
          } else if (err.status === 500) {
            this.notificationService.showError('Erreur serveur (500).');
          } else {
            this.notificationService.showError(`Erreur serveur (${err.status}).`);
          }
        }
      });
  }

  onSubmit() {
    if (this.registerForm.invalid || this.registerFirstForm.invalid || this.isLoadingStep2()) return;

    this.isLoadingStep2.set(true);

    const payload = {
      ...this.registerFirstForm.value,
      address: {
        street: this.addressControl.value,
        city: {
          id: this.cityIdSelected
        }
      }
    };

    this.userService.register(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res: UserProfileResponse) => {
          this.isLoadingStep2.set(false);
          this.userService.setUserProfile(res);
          this.router.navigate(['/home']).then();
        },
        error: () => {
          this.isLoadingStep2.set(false);
          this.notificationService.showError('Erreur lors de l\'inscription.');
        }
      });
  }

  goBackToStep1() {
    this.step.set(1);
  }


  private strictEmailValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const emailRegex = /^[\w.%+-]+@[\w.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(control.value)) return { invalidEmail: true };
    const parts = control.value.split('@');
    if (parts.length !== 2) return { invalidEmail: true };
    const domain = parts[1];
    if (!domain.includes('.')) return { invalidEmail: true };
    const domainParts = domain.split('.');
    if (domainParts.some((part: string) => part.length < 2)) return { invalidEmail: true };
    return null;
  }

  private validUsernameValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const usernameRegex = /^[\w-]+$/;
    return usernameRegex.test(control.value) ? null : { invalidUsername: true };
  }

  private strongPasswordValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;
    const hasUpperCase = /[A-Z]/.test(control.value);
    const hasLowerCase = /[a-z]/.test(control.value);
    const hasNumber = /\d/.test(control.value);
    const hasSpecialChar = /[@$!%*?&#]/.test(control.value);
    const errors: ValidationErrors = {};
    if (!hasUpperCase) errors['noUpperCase'] = true;
    if (!hasLowerCase) errors['noLowerCase'] = true;
    if (!hasNumber) errors['noNumber'] = true;
    if (!hasSpecialChar) errors['noSpecialChar'] = true;
    return Object.keys(errors).length > 0 ? errors : null;
  }
}
