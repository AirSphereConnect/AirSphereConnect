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
import {CityService} from '../../../services/city-service';
import {UserService} from '../../../services/user-service';
import {Subject, takeUntil} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Button} from '../button/button';
import {inputCitySearch} from '../../../utils/city-utils/city-utils';

@Component({
  selector: 'app-alerts-form',
  standalone: true,
  imports: [ReactiveFormsModule, Button],
  templateUrl: './alerts-form.html',
  styleUrl: './alerts-form.scss'
})
export class AlertsForm implements OnInit, OnChanges, OnDestroy {

  @Input() isOpen = signal(false);
  @Input() editingAlertsId: number | null = null;
  @Input() initialAlertsData: any = null;
  @Output() close = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly alertsService = inject(AlertsService);
  private readonly cityService = inject(CityService);
  private readonly userService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);

  alertsForm!: FormGroup;
  cityQuery = signal('');
  citySuggestions = signal<any[]>([]);
  cityIdSelected: number | null = null;
  errorMessage: string | null = null;
  isDeleteMode = false;

  private readonly destroy$ = new Subject<void>();

  citySearchEffect = inputCitySearch(this.cityService, this.cityQuery, this.citySuggestions);

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
      activeAlert: this.initialAlertsData.enabled === true,
      cityName: this.initialAlertsData.cityName || ''
    });
    this.cityIdSelected = this.initialAlertsData.cityId || null;
    this.isDeleteMode = false;
  }

  onCityInput(event: any) {
    this.cityQuery.set(event.target.value);
  }

  selectCity(city: any) {
    this.alertsForm.get('cityName')?.setValue(city.name);
    this.cityIdSelected = city.id;
    this.citySuggestions.set([]);
  }

  submitForm() {
    if (this.isDeleteMode) {
      if (!this.editingAlertsId) return;

      this.alertsService.deleteAlerts(this.editingAlertsId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: () => this.handleSuccess(),
          error: () => this.errorMessage = "Erreur lors de la suppression de l'alerte."
        });
      return;
    }

    const isNewEntry = !this.editingAlertsId;
    const cityIdValid = this.cityIdSelected !== null && this.cityIdSelected !== undefined;

    if (!this.alertsForm.valid || (isNewEntry && !cityIdValid)) {
      this.errorMessage = 'Veuillez remplir tous les champs et sélectionner une ville.';
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
        next: () => this.handleSuccess(),
        error: () => this.errorMessage = "Erreur lors de l'enregistrement de l'alerte."
      });
  }

  private handleSuccess() {
    this.errorMessage = null;
    this.alertsForm.reset();
    this.cityIdSelected = null;
    this.isDeleteMode = false;
    this.userService.fetchUserProfile();
    this.submitSuccess.emit();
    this.closeModal();
  }

  closeModal() {
    this.alertsForm.reset({
      activeAlert: false,
      cityName: ''
    });
    this.cityIdSelected = null;
    this.isDeleteMode = false;
    this.isOpen.set(false);
    this.close.emit();
  }
}
