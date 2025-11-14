import {Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, signal, inject, OnDestroy} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FavoritesService} from '../../../services/favorites-service';
import {CityService} from '../../../../core/services/city';
import {InputComponent} from '../input/input';
import {UserService} from '../../../services/user-service';
import {Subject, takeUntil} from 'rxjs';
import {Button} from '../button/button';
import {inputCitySearch} from '../../../utils/city-utils/city-utils';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {MatSelectModule} from '@angular/material/select';

interface catData {
  value: string;
  label: string;
}


@Component({
  selector: 'app-favorites-form',
  templateUrl: './favorites-form.html',
  styleUrls: ['./favorites-form.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, Button, ButtonCloseModal, InputComponent, MatSelectModule]
})
export class FavoritesForm implements OnInit, OnChanges, OnDestroy {
  @Input() isOpen = signal(false);
  @Input() editingFavoriteId: number | null = null;
  @Input() initialFavoriteData: any = null;
  @Output() close = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly favoritesService = inject(FavoritesService);
  private readonly cityService = inject(CityService);
  private readonly userService = inject(UserService);
  private readonly destroy$ = new Subject<void>();

  favoritesForm!: FormGroup;
  cityQuery = signal<string>('');
  citySuggestions = signal<any[]>([]);
  cityIdSelected: number | null = null;
  errorMessage: string | null = null;
  isDeleteMode = false;

  citySearchEffect = inputCitySearch(this.cityService, this.cityQuery, this.citySuggestions);

  ngOnInit() {
    this.favoritesForm = this.fb.group({
      activeWeather: [false, Validators.required],
      activeAirQuality: [false, Validators.required],
      activePopulation: [false, Validators.required],
      cityName: ['', Validators.required]
    });
  }

  /** ✅ Ajout : réagit quand initialData change (ex: ouverture modal en mode édition) */
  ngOnChanges(changes: SimpleChanges) {
    if (changes['initialFavoriteData'] && this.initialFavoriteData) {
      this.patchFormData();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private patchFormData() {
    this.favoritesForm.patchValue({
      activeWeather: this.initialFavoriteData?.enabled === true,
      activeAirQuality: this.initialFavoriteData?.enabled === true,
      activePopulation: this.initialFavoriteData?.enabled === true,
      cityName: this.initialFavoriteData?.cityName ?? ''
    });
    this.cityIdSelected = this.initialFavoriteData?.cityId ?? null;
    this.isDeleteMode = false;
  }

  onCityInput(event: any) {
    this.cityQuery.set(event.target.value);
  }


  selectCity(city: any) {
    this.favoritesForm.get('cityName')?.setValue(city.name);
    this.cityIdSelected = city.id;
    this.citySuggestions.set([]);
  }

  submitForm() {
    if (this.isDeleteMode) {
      if (!this.editingFavoriteId) return;
      this.favoritesService.deleteFavorites(this.editingFavoriteId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => this.handleSuccess(),
          error: () => this.errorMessage = "Erreur lors de la suppression du favori."
        });
      return;
    }

    const isNewEntry = !this.editingFavoriteId;
    const cityIdValid = this.cityIdSelected !== null && this.cityIdSelected !== undefined;

    if (!this.favoritesForm.valid || !this.favoritesForm.dirty || (isNewEntry && !cityIdValid)) {
      this.errorMessage = 'Veuillez modifier au moins un champ et sélectionner une ville.';
      return;
    }

    const payload = {
      enabledWeather: !!this.favoritesForm.value.activeWeather,
      enabledAirQuality: !!this.favoritesForm.value.activeAirQuality,
      enabledPopulation: !!this.favoritesForm.value.activePopulation,
      cityId: this.cityIdSelected
    };

    const request$ = this.editingFavoriteId
      ? this.favoritesService.editFavorites(payload, this.editingFavoriteId)
      : this.favoritesService.addFavorites(payload);

    request$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => this.handleSuccess(),
        error: () => (this.errorMessage = "Erreur lors de l'enregistrement du favori.")
      });
  }

  closeModal() {
    this.favoritesForm.reset({
      activeWeather: false,
      activeAirQuality: false,
      activePopulation: false,
      cityName: ''
    });
    this.cityIdSelected = null;
    this.isDeleteMode = false;
    this.isOpen.set(false);
    this.close.emit();
  }

  private handleSuccess() {
    this.errorMessage = null;
    this.favoritesForm.reset();
    this.cityIdSelected = null;
    this.isDeleteMode = false;
    this.userService.fetchUserProfile();
    this.submitSuccess.emit();
    this.closeModal();
  }
}
