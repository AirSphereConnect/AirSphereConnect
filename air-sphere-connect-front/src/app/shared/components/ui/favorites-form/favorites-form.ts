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
  OnDestroy
} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FavoritesService} from '../../../services/favorites-service';
import {CityService} from '../../../../core/services/city';
import {InputComponent} from '../input/input';
import {UserService} from '../../../services/user-service';
import {Subject, takeUntil} from 'rxjs';
import {Button} from '../button/button';
import {inputCitySearch} from '../../../utils/city-utils/city-utils';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';

@Component({
  selector: 'app-favorites-form',
  templateUrl: './favorites-form.html',
  styleUrls: ['./favorites-form.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, Button, ButtonCloseModal, InputComponent]
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
      favoriteCategory: ['', Validators.required],
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

      favoriteCategory: this.initialFavoriteData.favoriteCategory || '',
      cityName: this.initialFavoriteData.cityName || ''
    });
    this.cityIdSelected = this.initialFavoriteData.cityId || null;
    this.isDeleteMode = false;
    console.log('initialFavoriteData:', this.initialFavoriteData);
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
      favoriteCategory: this.favoritesForm.value.favoriteCategory,
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
      favoriteCategory: '',
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
