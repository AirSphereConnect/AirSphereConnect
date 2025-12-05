import {
  Component,
  DestroyRef,
  EventEmitter,
  inject,
  Input,
  numberAttribute,
  OnChanges,
  OnInit,
  Output,
  signal,
  SimpleChanges
} from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../services/user-service';
import { CityService } from '../../../../core/services/city';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Button } from '../button/button';
import { ButtonCloseModal } from '../button-close-modal/button-close-modal';
import { InputComponent } from '../input/input';
import { citySearch } from '../../../utils/city-search.util';
import { NotificationService } from '../../../services/notification-service';

@Component({
  selector: 'app-address-form',
  standalone: true,
  imports: [ReactiveFormsModule, Button, ButtonCloseModal, InputComponent],
  templateUrl: './address-form.html',
  styleUrl: './address-form.scss'
})
export class AddressForm implements OnInit, OnChanges {
  @Input() isOpen = signal(false);
  @Input({ transform: numberAttribute }) editingUserId!: number | undefined;
  @Input() addressData: any = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly cityService = inject(CityService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly notificationService = inject(NotificationService);

  addressForm!: FormGroup;
  cityQuery = signal('');
  citySuggestions = signal<any[]>([]);
  selectedCityId: number | null = null;
  isLoading = signal(false);


  // !! Obligatoire !!
  citySearchEffect = citySearch(this.cityService, this.cityQuery, this.citySuggestions);

  ngOnInit() {
    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      cityName: ['', Validators.required],
      cityId: [null, Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.addressForm && changes['addressData'] && this.addressData) {
      this.patchFormData();
    }
  }

  private patchFormData() {
    this.addressForm.patchValue({
      street: this.addressData.street,
      cityName: this.addressData.city?.name || '',
      cityId: this.addressData.city?.id || null
    });
    this.selectedCityId = this.addressData.city?.id || null;
  }

  onCityInput(event: any) {
    this.cityQuery.set(event.target.value);
  }

  selectCity(city: any) {
    this.addressForm.patchValue({
      cityName: city.name,
      cityId: city.id
    });
    this.selectedCityId = city.id;
    this.citySuggestions.set([]);
  }

  submit() {
    const isNewEntry = !this.editingUserId;
    const cityIdValid = this.selectedCityId !== null && this.selectedCityId !== undefined;

    if (!this.addressForm.valid || !this.addressForm.dirty || (isNewEntry && !cityIdValid)) {
      this.notificationService.showError('Veuillez modifier au moins un champ et sélectionner une ville.');
      return;
    }

    if (this.addressForm.invalid) return;
    this.isLoading.set(true);

    if (!this.selectedCityId) {
      this.isLoading.set(false);
      return;
    }

    const payload = {
      street: this.addressForm.get('street')?.value,
      city: { id: this.selectedCityId }
    };

    this.userService.editAddress(this.addressData.id, payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.showSuccess('Adresse modifier avec succès');
          this.isLoading.set(false);
          this.updated.emit();
          this.onCloseModal();
        },
        error: () => {
          this.isLoading.set(false);
          this.notificationService.showError('Erreur lors de la mise à jour.');
        }
      });
  }

  onCloseModal() {
    this.addressForm.reset();
    this.selectedCityId = null;
    this.isOpen.set(false);
    this.closeModal.emit();
  }
}
