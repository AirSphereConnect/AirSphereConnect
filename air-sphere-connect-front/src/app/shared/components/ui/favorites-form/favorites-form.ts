import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnChanges,
  SimpleChanges,
  signal,
  inject,
  OnDestroy,
  DestroyRef
} from '@angular/core';
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
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

interface FavoriteFormData {
  cityId: number;
  cityName: string;
  selectWeather: boolean;
  selectAirQuality: boolean;
  selectPopulation: boolean;
}


@Component({
  selector: 'app-favorites-form',
  templateUrl: './favorites-form.html',
  styleUrls: ['./favorites-form.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, Button, ButtonCloseModal, InputComponent, MatSelectModule]
})
export class FavoritesForm implements OnInit, OnChanges {

  @Input() isOpen = signal(false);
  @Input() editingFavoriteId: number | null = null;
  @Input() initialFavoriteData: FavoriteFormData | null = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly favoritesService = inject(FavoritesService);
  private readonly cityService = inject(CityService);
  private readonly userService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly errorMessageService = inject(ErrorMessageService);

  favoritesForm!: FormGroup;
  cityQuery = signal<string>('');
  citySuggestions = signal<City[]>([]);
  isLoading = signal(false);
  cityIdSelected: number | null = null;
  isDeleteMode = false;

  private readonly destroy$ = new Subject<void>();

  citySearchEffect = citySearch(this.cityService, this.cityQuery, this.citySuggestions);

  ngOnInit() {
    this.favoritesForm = this.fb.group({
      activeWeather: [false, Validators.required],
      activeAirQuality: [false, Validators.required],
      activePopulation: [false, Validators.required],
      cityName: ['', Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.favoritesForm && changes['initialFavoriteData'] && this.initialFavoriteData) {
      this.patchFormData();
    }
  }

  private patchFormData() {
    if (this.initialFavoriteData) {
      this.favoritesForm.patchValue({
        activeWeather: this.initialFavoriteData.selectWeather,
        activeAirQuality: this.initialFavoriteData.selectAirQuality,
        activePopulation: this.initialFavoriteData.selectPopulation,
        cityName: this.initialFavoriteData.cityName || ''
      });
    }
    this.cityIdSelected = this.initialFavoriteData!.cityId || null;
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
        .pipe(takeUntilDestroyed(this.destroyRef))
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
      .pipe(takeUntilDestroyed(this.destroyRef))
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

  onCloseModal() {
    this.favoritesForm.reset({
      activeWeather: false,
      activeAirQuality: false,
      activePopulation: false,
      cityName: ''
    });
    this.cityIdSelected = null;
    this.isDeleteMode = false;
    this.isOpen.set(false);
    this.closeModal.emit();
  }

  private handleSuccess() {
    this.favoritesForm.reset();
    this.cityIdSelected = null;
    this.isDeleteMode = false;
    this.userService.fetchUserProfile();
    this.submitSuccess.emit();
    this.onCloseModal();
  }
}
