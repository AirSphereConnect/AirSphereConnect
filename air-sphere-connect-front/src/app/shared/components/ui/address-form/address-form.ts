import {Component, EventEmitter, Input, OnChanges, OnInit, Output, signal} from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../../services/user-service';
import { CityService } from '../../../../core/services/city';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-address-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './address-form.html',
})
export class AddressForm implements OnChanges {
  @Input() isOpen = signal(false);
  @Input() editingUserId!: number | null;
  @Input() addressData: any = null;
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<void>();

  addressForm: FormGroup;
  citySuggestions: any[] = [];
  selectedCityId: number | null = null;

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private cityService: CityService
  ) {
    this.addressForm = this.fb.group({
      street: ['', Validators.required],
      cityName: ['', Validators.required],
      cityId: [null, Validators.required]
    });
  }

  ngOnChanges() {
    if (this.addressData) {
      this.addressForm.patchValue({
        street: this.addressData.street,
        cityName: this.addressData.city?.name || '',
        cityId: this.addressData.city?.id || null
      });
      this.selectedCityId = this.addressData.city?.id || null;
    }

    // Suivi des changements pour suggestions
    this.addressForm.get('cityName')?.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(query => query && query.length > 1 ? this.cityService.searchCities(query) : [])
      )
      .subscribe({
        next: (cities) => this.citySuggestions = cities || [],
        error: () => this.citySuggestions = []
      });
  }

  selectCity(city: any) {
    this.addressForm.patchValue({
      cityName: city.name,
      cityId: city.id
    });
    this.selectedCityId = city.id;
    this.citySuggestions = [];
  }

  submit() {
    if (this.addressForm.invalid) return;
    this.isLoading.set(true);

    const payload = {
      street: this.addressForm.get('street')?.value,
      city: { id: this.selectedCityId }
    };
    console.log("user" + this.editingUserId)

    this.userService.editAddress(this.editingUserId, payload).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.updated.emit();
        this.close.emit();
      },
      error: () => {
        this.isLoading.set(false);
        this.errorMessage.set('Erreur lors de la mise à jour.');
      }
    });
  }
}
