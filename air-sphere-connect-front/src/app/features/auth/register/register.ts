import {Component, computed, OnInit, signal} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {UserService} from '../../../shared/services/user-service';
import {Router, RouterLink} from '@angular/router';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs';
import {CityService} from '../../../shared/services/city-service';
import {InputComponent} from '../../../shared/components/ui/input/input';
import {Button} from '../../../shared/components/ui/button/button';
import {IconComponent} from '../../../shared/components/ui/icon/icon';
import {AsyncPipe} from '@angular/common';
import {HeroIconName} from '../../../shared/icons/heroicons.registry';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    InputComponent,
    Button,
    InputComponent,
    RouterLink,
    IconComponent,
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class Register implements OnInit {
  step = signal<number>(1); // 🎯 Converti en signal
  registerForm!: FormGroup;
  registerFirstForm!: FormGroup;
  citySuggestions: any[] = [];
  cityIdSelected: number | null = null;

  // 🎯 Signals pour les états
  errorMessage = signal<string | null>(null);
  isLoadingStep1 = signal<boolean>(false);
  isLoadingStep2 = signal<boolean>(false);
  showPassword = signal<boolean>(false);

  passwordVisible = signal(false);


  // 🎯 Computed signals pour les validations
  canSubmitStep1 = signal<boolean>(false);
  canSubmitStep2 = signal<boolean>(false);

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private cityService: CityService,
    private router: Router
  ) {}

  ngOnInit() {
    this.registerFirstForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(20)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]]
    });

    this.registerForm = this.fb.group({
      address: ['', Validators.required],
      cityName: ['', Validators.required],
      cityCode: ['', Validators.required]
    });

    this.registerFirstForm.statusChanges.subscribe(() => {
      this.canSubmitStep1.set(this.registerFirstForm.valid);
    });
    this.canSubmitStep1.set(this.registerFirstForm.valid);

    this.registerForm.statusChanges.subscribe(() => {
      this.canSubmitStep2.set(this.registerForm.valid);
    });
    this.canSubmitStep2.set(this.registerForm.valid);

    // Recherche de villes
    this.cityNameControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) =>
          query && query.length > 1
            ? this.cityService.searchCities(query)
            : []
        )
      )
      .subscribe({
        next: (cities) => {
          console.log('Suggestions reçues:', cities);
          this.citySuggestions = cities || [];
        },
        error: (err) => {
          console.error('Erreur lors de la recherche de ville:', err);
          this.citySuggestions = [];
        }
      });
  }

  get usernameControl() {
    return this.registerFirstForm.get('username') as FormControl;
  }

  get emailControl() {
    return this.registerFirstForm.get('email') as FormControl;
  }

  get passwordControl() {
    return this.registerFirstForm.get('password') as FormControl;
  }

  get addressControl() {
    return this.registerForm.get('address') as FormControl;
  }

  get cityNameControl() {
    return this.registerForm.get('cityName') as FormControl;
  }

  get cityCodeControl() {
    return this.registerForm.get('cityCode') as FormControl;
  }


  togglePasswordVisibility() {
    this.passwordVisible.set(!this.passwordVisible());
    console.log('Password visible:', this.passwordVisible());
  }

  passwordIcon = computed<HeroIconName>(() => this.passwordVisible() ? 'eyeSlash' : 'eye');
  passwordType = computed(() => this.passwordVisible() ? 'text' : 'password');

  onFirstSubmit() {
    if (this.registerFirstForm.invalid || this.isLoadingStep1()) return;

    this.isLoadingStep1.set(true); // 🔥 Début du chargement
    this.errorMessage.set(null);   // 🔥 Réinitialiser l'erreur

    const {username, email} = this.registerFirstForm.value;

    this.userService.checkAvailability(username, email).subscribe({
      next: (res) => {
        this.isLoadingStep1.set(false); // 🔥 Fin du chargement

        if (res.usernameTaken) {
          this.errorMessage.set("Nom d'utilisateur déjà pris.");
        } else if (res.emailTaken) {
          this.errorMessage.set("Adresse email déjà utilisée.");
        } else {
          this.errorMessage.set(null);
          this.step.set(2); // 🔥 Passer à l'étape 2
        }
      },
      error: (err) => {
        this.isLoadingStep1.set(false); // 🔥 Fin du chargement
        console.error('❌ Erreur:', err);

        if (err.status === 0) {
          this.errorMessage.set("Impossible de contacter le serveur.");
        } else if (err.status === 404) {
          this.errorMessage.set("Service non disponible.");
        } else if (err.status === 500) {
          this.errorMessage.set("Erreur serveur (500).");
        } else {
          this.errorMessage.set(`Erreur serveur (${err.status}).`);
        }
      }
    });
  }

  onCityInput(event: any) {
    const query = event.target.value;
    if (query.length < 2) return;
    this.cityService.searchCities(query).subscribe({
      next: (cities) => this.citySuggestions = cities
    });
  }

  selectCity(city: any) {
    this.cityNameControl.setValue(city.name);
    this.registerForm.get('cityCode')?.setValue(city.postalCode);
    this.cityIdSelected = city.id;
    this.citySuggestions = [];
  }


  onSubmit() {
    if (this.registerForm.invalid || this.registerFirstForm.invalid || this.isLoadingStep2()) return;

    this.isLoadingStep2.set(true);
    this.errorMessage.set(null);

    const payload = {
      ...this.registerFirstForm.value,
      address: {
        street: this.addressControl.value,
        city: {
          id: this.cityIdSelected
        }
      }
    };

    this.userService.register(payload).subscribe({
      next: (res: any) => {
        this.isLoadingStep2.set(false); // 🔥 Fin du chargement
        this.userService.setUserProfile(res);
        this.router.navigate(['/home']).then();
      },
      error: (err) => {
        this.isLoadingStep2.set(false); // 🔥 Fin du chargement
        this.errorMessage.set("Erreur lors de l'inscription.");
      }
    });
  }

  goBackToStep1() {
    this.step.set(1);
    this.errorMessage.set(null);
  }

  clearError() {
    this.errorMessage.set(null);
  }
}
