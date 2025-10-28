import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FavoritesService } from '../../../services/favorites-service';
import { CityService } from '../../../services/city-service';
import { InputComponent } from '../input/input';
import {UserService} from '../../../services/user-service';

@Component({
  selector: 'app-favorites-form',
  templateUrl: './favorites-form.html',
  styleUrls: ['./favorites-form.scss'],
  standalone: true,
  imports: [ReactiveFormsModule]
})
export class FavoritesForm implements OnInit, OnChanges {
  @Input() isOpen = signal(false);
  @Input() editingFavoriteId: number | null = null;
  @Input() initialFavoriteData: any = null;
  @Output() close = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  favoritesForm!: FormGroup;
  citySuggestions: any[] = [];
  cityIdSelected: number | null = null;
  errorMessage: string | null = null;
  isDeleteMode = false;

  constructor(
    private fb: FormBuilder,
    private favoritesService: FavoritesService,
    private cityService: CityService,
    private userService: UserService
  ) {}

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

  private patchFormData() {
    this.favoritesForm.patchValue({
      favoriteCategory: this.initialFavoriteData.favoriteCategory || '',
      cityName: this.initialFavoriteData.cityName || ''
    });
    this.cityIdSelected = this.initialFavoriteData.cityId || null;
    this.isDeleteMode = false;
  }

  onCityInput(event: any) {
    const query = event.target.value;
    if (query.length < 2) {
      this.citySuggestions = [];
      return;
    }

    this.cityService.searchCities(query).subscribe({
      next: (cities) => (this.citySuggestions = cities || []),
      error: () => (this.citySuggestions = [])
    });
  }

  selectCity(city: any) {
    this.favoritesForm.get('cityName')?.setValue(city.name);
    this.cityIdSelected = city.id;
    this.citySuggestions = [];
  }

  submitForm() {
    if (this.isDeleteMode) {
      if (!this.editingFavoriteId) return;
      this.favoritesService.deleteFavorites(this.editingFavoriteId).subscribe({
        next: () => this.handleSuccess(),
        error: () => this.errorMessage = "Erreur lors de la suppression de l'alerte."
      });
      return;
    }

    if (!this.favoritesForm.valid || !this.cityIdSelected) {
      this.errorMessage = 'Veuillez remplir tous les champs et sélectionner une ville.';
      return;
    }

    const payload = {
      favoriteCategory: this.favoritesForm.value.favoriteCategory,
      cityId: this.cityIdSelected
    };

    const request$ = this.editingFavoriteId
      ? this.favoritesService.editFavorites(payload, this.editingFavoriteId)
      : this.favoritesService.addFavorites(payload);

    request$.subscribe({
      next: () => {
        this.errorMessage = null;
        this.favoritesForm.reset();
        this.cityIdSelected = null;
        this.submitSuccess.emit();
        this.closeModal();
      },
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
