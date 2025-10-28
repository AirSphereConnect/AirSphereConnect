import {
  Component,
  computed,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  signal,
  SimpleChanges
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import { UserService } from '../../../services/user-service';
import { CityService } from '../../../services/city-service';
import { HeroIconName } from '../../../icons/heroicons.registry';


@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './user-form.html',
  styleUrls: ['./user-form.scss']
})
export class UserForm implements OnInit, OnChanges {
  @Input() isOpen = signal(false);
  @Input() editingUserId!: number | null;
  @Input() initialUserData: any = null; // données existantes
  @Output() close = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  step = signal(1);

  userForm!: FormGroup; // username, email, password
  addressForm!: FormGroup; // adresse, ville, postalCode
  citySuggestions: any[] = [];
  cityIdSelected: number | null = null;

  // États
  errorMessage = signal<string | null>(null);
  isLoadingStep1 = signal(false);
  isLoadingStep2 = signal(false);
  passwordVisible = signal(false);

  // Computed signals
  passwordType = computed(() => this.passwordVisible() ? 'text' : 'password');
  passwordIcon = computed<HeroIconName>(() => this.passwordVisible() ? 'eyeSlash' : 'eye');

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private cityService: CityService,
  ) {
    // Initialisation vide
    this.userForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['']
    });

    this.addressForm = this.fb.group({
      address: ['', Validators.required],
      cityName: ['', Validators.required],
      cityCode: [{ value: '', disabled: true }]
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['initialUserData'] && this.initialUserData) {
      // Met à jour les valeurs du formulaire
      this.userForm.patchValue({
        username: this.initialUserData.username,
        email: this.initialUserData.email,
        password: '' // vide par défaut
      });

      this.addressForm.patchValue({
        address: this.initialUserData.address?.street || '',
        cityName: this.initialUserData.address?.city?.name || '',
        cityCode: this.initialUserData.address?.city?.postalCode || ''
      });
    }
  }

  ngOnInit() {
    // Step 1
    this.userForm = this.fb.group({
      username: [this.initialUserData?.username || '', [Validators.required, Validators.minLength(1), Validators.maxLength(20)]],
      email: [this.initialUserData?.email || '', [Validators.required, Validators.email]],
      password: ['']
    });

    // Step 2
    this.addressForm = this.fb.group({
      address: [this.initialUserData?.address?.street || '', Validators.required],
      cityName: [this.initialUserData?.address?.city?.name || '', Validators.required],
      cityCode: [{ value: this.initialUserData?.address?.city?.postalCode || '', disabled: true }, Validators.required]
    });

    // Suggestions villes
    this.cityNameControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged(), switchMap(query =>
        query && query.length > 1 ? this.cityService.searchCities(query) : []
      ))
      .subscribe({
        next: cities => this.citySuggestions = cities || [],
        error: () => this.citySuggestions = []
      });
  }

  // Getters
  get usernameControl() { return this.userForm.get('username') as FormControl; }
  get emailControl() { return this.userForm.get('email') as FormControl; }
  get passwordControl() { return this.userForm.get('password') as FormControl; }
  get addressControl() { return this.addressForm.get('address') as FormControl; }
  get cityNameControl() { return this.addressForm.get('cityName') as FormControl; }
  get cityCodeControl() { return this.addressForm.get('cityCode') as FormControl; }

  togglePasswordVisibility() { this.passwordVisible.set(!this.passwordVisible()); }

  onCityInput(event: any) {
    const query = event.target.value;
    if (query.length < 2) return;
    this.cityService.searchCities(query).subscribe({
      next: (cities) => this.citySuggestions = cities
    });
  }

  // Sélection ville
  selectCity(city: any) {
    this.cityNameControl.setValue(city.name);
    this.cityCodeControl.setValue(city.postalCode);
    this.cityIdSelected = city.id;
    this.citySuggestions = [];
  }

  // Étape 1: Vérifier username/email
  onStep1Submit() {
    if (this.userForm.invalid || this.isLoadingStep1()) return;
    this.isLoadingStep1.set(true);
    this.errorMessage.set(null);

    const { username, email } = this.userForm.value;

    this.userService.checkAvailability(username, email).subscribe({
      next: res => {
        this.isLoadingStep1.set(false);
        if (res.usernameTaken && username !== this.initialUserData?.username) {
          this.errorMessage.set('Nom d’utilisateur déjà utilisé.');
          return;
        }
        if (res.emailTaken && email !== this.initialUserData?.email) {
          this.errorMessage.set('Email déjà utilisé.');
          return;
        }
        this.errorMessage.set(null);
        this.step.set(2); // passer à step 2
      },
      error: () => {
        this.isLoadingStep1.set(false);
        this.errorMessage.set('Erreur lors de la vérification du username/email.');
      }
    });
  }

  // Étape 2: Modifier adresse + envoyer payload final
  onSubmit() {
    if (this.userForm.invalid || this.addressForm.invalid || this.isLoadingStep2()) return;
    this.isLoadingStep2.set(true);
    this.errorMessage.set(null);

    const payload: any = {
      ...this.userForm.value,
      address: {
        street: this.addressControl.value,
        city: { id: this.cityIdSelected }
      }
    };

    if (!payload.password) delete payload.password;

    this.userService.editUser(this.initialUserData?.id, payload).subscribe({
      next: () => {
        this.isLoadingStep2.set(false);
        this.submitSuccess.emit();
        this.close.emit();
      },
      error: () => {
        this.isLoadingStep2.set(false);
        this.errorMessage.set('Erreur lors de la mise à jour.');
      }
    });
  }

  goBackToStep1() { this.step.set(1); this.errorMessage.set(null); }
  clearError() { this.errorMessage.set(null); }
}
