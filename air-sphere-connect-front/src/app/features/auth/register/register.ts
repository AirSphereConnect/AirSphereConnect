import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {InputField} from '../../../shared/components/input-field/input-field';
import {PasswordField} from '../../../shared/components/password-field/password-field';
import {UserService} from '../../../shared/services/user-service';
import {Router} from '@angular/router';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs';
import {CityService} from '../../../shared/services/city-service';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    InputField,
    PasswordField,
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class Register implements OnInit {
  step = 1;
  registerForm!: FormGroup;
  registerFirstForm!: FormGroup;
  citySuggestions: any[] = [];
  cityIdSelected: number | null = null;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private cityService: CityService,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.registerFirstForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    this.registerForm = this.fb.group({
      address: ['', Validators.required],
      cityName: ['', Validators.required],
      cityCode: ['', Validators.required]
    });

    // 👇 Détection en temps réel des saisies sur le champ "cityName"
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

  onFirstSubmit() {
    if (this.registerFirstForm.invalid) return;
    const {username, email} = this.registerFirstForm.value;

    this.userService.checkAvailability(username, email).subscribe({
      next: (res) => {
        if (res.usernameTaken) this.errorMessage = "Nom d'utilisateur déjà pris.";
        else if (res.emailTaken) this.errorMessage = "Adresse email déjà utilisée.";
        else {
          this.errorMessage = null;
          this.step = 2; // ✅ passe à l’étape suivante
        }
      },
      error: () => this.errorMessage = "Erreur serveur."
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
    if (this.registerForm.invalid || this.registerFirstForm.invalid) return;

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
        // ✅ Met à jour le profil local avec la réponse du backend
        this.userService.setUserProfile(res);

        // Naviguer ou mettre à jour l'état de l'application
        this.router.navigate(['/home']);
      },
      error: () => this.errorMessage = "Erreur lors de l'inscription."
    });
  }
}
