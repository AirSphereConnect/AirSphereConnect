import {Component, OnInit} from '@angular/core';
import {FavoritesService} from '../../../../shared/services/favorites-service';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {InputField} from '../../../../shared/components/input-field/input-field';
import {debounceTime, distinctUntilChanged, switchMap} from 'rxjs';
import {CityService} from '../../../../shared/services/city-service';

@Component({
  selector: 'app-favorites-add',
  imports: [
    ReactiveFormsModule,
    InputField
  ],
  templateUrl: './favorites-add.html',
  styleUrl: './favorites-add.scss'
})
export class FavoritesAdd implements OnInit {
  addFavoritesForm!: FormGroup;
  errorMessage: string | null = null;

  citySuggestions: any[] = [];
  cityIdSelected: number | null = null;

  constructor(private favoritesService: FavoritesService,
              private router: Router,
              private fb: FormBuilder,
              private cityService: CityService) {
  }

  ngOnInit() {
    this.addFavoritesForm = this.fb.group({
      cityName: ['', Validators.required],
      type: ['', Validators.required],  // Ajout du contrôle 'type' dans le formulaire
    });

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
          this.citySuggestions = cities || [];
        },
        error: () => {
          this.citySuggestions = [];
        }
      });
  }

  get cityNameControl() {
    return this.addFavoritesForm.get('cityName') as FormControl;
  }

  get favoriteCategoryControl() {
    return this.addFavoritesForm.get('favoriteCategory') as FormControl;
  }

  selectCity(city: any) {
    this.cityNameControl.setValue(city.name);
    this.cityIdSelected = city.id;
    this.citySuggestions = [];
  }

  onCityInput(event: any) {
    const query = event.target.value;
    if (query.length < 2) return;
    this.cityService.searchCities(query).subscribe({
      next: (cities) => this.citySuggestions = cities
    });
  }

  addFavorite() {
    if (this.addFavoritesForm.valid && this.cityIdSelected !== null) {
      const payload = {
        city: {
          id: this.cityIdSelected
        },
        favoriteCategory: this.addFavoritesForm.get('favoriteCategory')?.value,
      };

      this.favoritesService.addFavorites(payload).subscribe({
        next: () => {
          this.errorMessage = null;
          this.router.navigate(['/auth/profile']);
        },
        error: () => {
          this.errorMessage = "[translate:Erreur lors de la création du favori.]";
        }
      });
    } else {
      this.errorMessage = "[translate:Veuillez sélectionner une ville et remplir tous les champs obligatoires.]";
    }
  }

}
