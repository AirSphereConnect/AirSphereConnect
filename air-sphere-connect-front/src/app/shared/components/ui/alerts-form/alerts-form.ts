import { Component, EventEmitter, Input, OnChanges, OnInit, Output, signal, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { AlertsService } from '../../../services/alerts-service';
import { CityService } from '../../../services/city-service';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-alerts-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './alerts-form.html',
  styleUrl: './alerts-form.scss'
})
export class AlertsForm implements OnInit, OnChanges {
  @Input() isOpen = signal(false);
  @Input() editingAlertsId: number | null = null;
  @Input() initialAlertsData: any = null;
  @Output() close = new EventEmitter<void>();
  @Output() submitSuccess = new EventEmitter<void>();

  alertsForm!: FormGroup;
  citySuggestions: any[] = [];
  cityIdSelected: number | null = null;
  errorMessage: string | null = null;
  isDeleteMode = false;

  constructor(
    private fb: FormBuilder,
    private alertsService: AlertsService,
    private cityService: CityService,
    private userService: UserService
  ) {}

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

  private patchFormData() {
    this.alertsForm.patchValue({
      activeAlert: this.initialAlertsData.enabled === true,
      cityName: this.initialAlertsData.cityName || ''
    });
    this.cityIdSelected = this.initialAlertsData.cityId || null;
    this.isDeleteMode = false; // Reset delete mode when modal opens
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
    this.alertsForm.get('cityName')?.setValue(city.name);
    this.cityIdSelected = city.id;
    this.citySuggestions = [];
  }

  submitForm() {
    if (this.isDeleteMode) {
      if (!this.editingAlertsId) return;
      this.alertsService.deleteAlerts(this.editingAlertsId).subscribe({
        next: () => this.handleSuccess(),
        error: () => this.errorMessage = "Erreur lors de la suppression de l'alerte."
      });
      return;
    }

    if (!this.alertsForm.valid || !this.cityIdSelected) {
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

    request$.subscribe({
      next: () => this.handleSuccess(),
      error: () => this.errorMessage = "Erreur lors de l'enregistrement de l'alerte."
    });
  }

  onDelete() {
    this.isDeleteMode = true;
    this.submitForm();
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
