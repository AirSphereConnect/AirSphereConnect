import {Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, signal, inject, OnDestroy} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FavoritesService} from '../../../services/favorites-service';
import {CityService} from '../../../../core/services/city';
import {InputComponent} from '../input/input';
import {UserService} from '../../../services/user-service';
import {Subject, takeUntil} from 'rxjs';
import {Button} from '../button/button';
import {citySearch} from '../../../utils/city-search.util';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {MatSelectModule} from '@angular/material/select';
import {City} from '../../../../core/models/city.model';
import {ErrorMessageService} from '../../../services/error-message-service';

interface FavoriteFormData {
  cityId: number;
  cityName: string;
  enabled: boolean;
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
  @Input() initialFavoriteData: FavoriteFormData | null = null;
  @Output() closeEvent = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  private readonly destroy$ = new Subject<void>();
  private readonly fb = inject(FormBuilder);
  private readonly favoritesService = inject(FavoritesService);
  private readonly cityService = inject(CityService);
  private readonly userService = inject(UserService);
  private readonly errorMessageService = inject(ErrorMessageService);

  favoritesForm!: FormGroup;
  cityQuery = signal<string>('');
  citySuggestions = signal<City[]>([]);
  isLoading = signal(false);
  cityIdSelected: number | null = null;
  errorMessage: string | null = null;
  isDeleteMode = false;

  citySearchEffect = citySearch(this.cityService, this.cityQuery, this.citySuggestions);

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
      activeWeather: this.initialFavoriteData?.enabled,
      activeAirQuality: this.initialFavoriteData?.enabled,
      activePopulation: this.initialFavoriteData?.enabled,
      cityName: this.initialFavoriteData?.cityName ?? ''
    });
    this.cityIdSelected = this.initialFavoriteData?.cityId ?? null;
    this.isDeleteMode = false;
  }

  onCityInput(event: Event) {
    const target = event.target as HTMLInputElement;
    this.cityQuery.set(target.value);
  }


  selectCity(city: City) {
    this.favoritesForm.get('cityName')?.setValue(city.name);
    this.cityIdSelected = city.id;
    this.citySuggestions.set([]);
  }

  submitForm() {
    const isNewEntry = !this.editingFavoriteId;
    const cityIdValid = this.cityIdSelected !== null && this.cityIdSelected !== undefined;

    if (!this.favoritesForm.valid || !this.favoritesForm.dirty || (isNewEntry && !cityIdValid)) {
      this.errorMessageService.setMessage('Veuillez modifier au moins un champ et sélectionner une ville.');
      return;
    }

    if (this.favoritesForm.invalid) return;
    this.isLoading.set(true);

    if (this.isDeleteMode) {
      if (!this.editingFavoriteId) return;
      this.favoritesService.deleteFavorites(this.editingFavoriteId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.handleSuccess();
            this.isLoading.set(false);
          },
          error: () => {
            this.errorMessageService.setMessage("Erreur lors de la suppression du favori.");
            this.isLoading.set(false);
          }
        });
      return;
    }

    const payload = {
      selectWeather: !!this.favoritesForm.value.activeWeather,
      selectAirQuality: !!this.favoritesForm.value.activeAirQuality,
      selectPopulation: !!this.favoritesForm.value.activePopulation,
      cityId: this.cityIdSelected
    };

    const request$ = this.editingFavoriteId
      ? this.favoritesService.editFavorites(payload, this.editingFavoriteId)
      : this.favoritesService.addFavorites(payload);

    request$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.handleSuccess();
          this.isLoading.set(false);
        },
        error: () => {
          this.errorMessageService.setMessage("Erreur lors de l'enregistrement du favori.");
          this.isLoading.set(false);
        }
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
    this.closeEvent.emit();
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
