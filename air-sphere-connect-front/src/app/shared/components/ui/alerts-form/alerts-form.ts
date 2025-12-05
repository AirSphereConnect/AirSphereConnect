import {
  Component, DestroyRef,
  EventEmitter, inject,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  signal,
  SimpleChanges
} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AlertsService} from '../../../services/alerts-service';
import {CityService} from '../../../../core/services/city';
import {UserService} from '../../../services/user-service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Button} from '../button/button';
import {citySearch} from '../../../utils/city-search.util';
import {ButtonCloseModal} from '../button-close-modal/button-close-modal';
import {InputComponent} from '../input/input';
import {Subject} from 'rxjs';
import {City} from '../../../../core/models/city.model';

interface AlertFormData {
  cityId: number;
  cityName: string;
  enabled: boolean;
}
import {NotificationService} from '../../../services/notification-service';

@Component({
  selector: 'app-alerts-form',
  standalone: true,
  imports: [ReactiveFormsModule, Button, ButtonCloseModal, InputComponent],
  templateUrl: './alerts-form.html',
  styleUrl: './alerts-form.scss'
})
export class AlertsForm implements OnInit, OnChanges, OnDestroy {

  @Input() isOpen = signal(false);
  @Input() editingAlertsId: number | null = null;
  @Input() initialAlertsData: AlertFormData | null = null;
  @Output() closeModal = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly alertsService = inject(AlertsService);
  private readonly cityService = inject(CityService);
  private readonly userService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly notificationService = inject(NotificationService);

  alertsForm!: FormGroup;
  cityQuery = signal('');
  citySuggestions = signal<City[]>([]);
  isLoading = signal(false);
  cityIdSelected: number | null = null;
  isDeleteMode = false;

  private readonly destroy$ = new Subject<void>();

  // !! Obligatoire !!
  citySearchEffect = citySearch(this.cityService, this.cityQuery, this.citySuggestions);

  ngOnInit() {
    this.alertsForm = this.fb.group({
      activeAlert: [false, Validators.required],
      cityName: ['', Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.alertsForm && changes['initialAlertsData'] && this.initialAlertsData) {
      this.patchFormData();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private patchFormData() {
    this.alertsForm.patchValue({
      activeAlert: this.initialAlertsData?.enabled,
      cityName: this.initialAlertsData?.cityName || ''
    });
    this.cityIdSelected = this.initialAlertsData?.cityId || null;
    this.isDeleteMode = false;
  }

  onCityInput(event: Event) {
    const target = event.target as HTMLInputElement;
    this.cityQuery.set(target.value);
  }

  selectCity(city: City) {
    this.alertsForm.get('cityName')?.setValue(city.name);
    this.cityIdSelected = city.id;
    this.citySuggestions.set([]);
  }

  submitForm() {
    const isNewEntry = !this.editingAlertsId;
    const cityIdValid = this.cityIdSelected !== null && this.cityIdSelected !== undefined;

    if (!this.alertsForm.valid || !this.alertsForm.dirty || (isNewEntry && !cityIdValid)) {
      this.notificationService.showError('Veuillez modifier au moins un champ et sélectionner une ville.');
      return;
    }

    if (this.alertsForm.invalid) return;
    this.isLoading.set(true);

    if (this.isDeleteMode) {
      if (!this.editingAlertsId) return;

      this.alertsService.deleteAlerts(this.editingAlertsId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => {
            this.notificationService.showSuccess('Alerte supprimée avec succès');
            this.handleSuccess();
            this.isLoading.set(false);
          },
          error: () => {{
            this.notificationService.showError("Erreur lors de la suppression de l'alerte.");
            this.isLoading.set(false);
          }}
        });
      return;
    }


    const payload = {
      enabled: !!this.alertsForm.value.activeAlert,
      cityId: this.cityIdSelected
    };

    const request$ = this.editingAlertsId
      ? this.alertsService.editAlerts(payload, this.editingAlertsId)
      : this.alertsService.addAlerts(payload);

    request$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(isNewEntry ? 'Alerte ajoutée avec succès' : 'Alerte modifiée avec succès');
          this.handleSuccess()
          this.isLoading.set(false);
        },
        error: () => {
          this.notificationService.showError("Erreur lors de l'enregistrement de l'alerte.")
          this.isLoading.set(false);
        }
      });
  }

  private handleSuccess() {
    this.alertsForm.reset();
    this.cityIdSelected = null;
    this.isDeleteMode = false;
    this.userService.fetchUserProfile();
    this.submitSuccess.emit();
    this.onClose();
  }

  onClose() {
    this.alertsForm.reset({
      activeAlert: false,
      cityName: ''
    });
    this.cityIdSelected = null;
    this.isDeleteMode = false;
    this.isOpen.set(false);
    this.closeModal.emit();
  }
}
