import {
  Component,
  DestroyRef,
  inject,
  OnInit,
  AfterViewInit,
  signal,
  ViewChild,
  TemplateRef
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UserService } from '../../shared/services/user-service';
import { CityService } from '../../core/services/city';
import { DataOrchestratorService } from '../../core/services/data-orchestrator';
import { ApiConfigService } from '../../core/services/api';
import { City, CityHistoryData } from '../../core/models/city.model';
import { Button } from '../../shared/components/ui/button/button';
import { AirQualityHistory } from './components/air-quality-history/air-quality-history';
import { WeatherHistory } from './components/weather-history/weather-history';
import { IconComponent } from '../../shared/components/ui/icon/icon';
import { InputComponent } from '../../shared/components/ui/input/input';
import { Tab, type TabItem } from '../../shared/components/ui/tab/tab';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    Button,
    AirQualityHistory,
    WeatherHistory,
    IconComponent,
    InputComponent,
    Tab
  ],
  templateUrl: './history.html',
  styleUrls: ['./history.scss']
})
export class History implements OnInit, AfterViewInit {
  private userService = inject(UserService);
  private cityService = inject(CityService);
  private orchestrator = inject(DataOrchestratorService);
  private apiConfig = inject(ApiConfigService);
  private destroyRef = inject(DestroyRef);
  private fb = inject(FormBuilder);

  // FormGroup pour les filtres
  filterForm!: FormGroup;

  // Signals
  selectedCityName = signal<string>('');
  selectedInseeCode = signal<string>('');
  citySearchInput = signal<string>('');
  startDate = signal<string>('');
  endDate = signal<string>('');
  activeTab = signal<'air-quality' | 'weather'>('air-quality');

  cities = signal<City[]>([]);
  filteredCities = signal<City[]>([]);
  showDropdown = signal(false);
  historyData = signal<CityHistoryData | null>(null);
  isLoading = signal(false);
  error = signal<string | null>(null);
  private isInitializing = true;


  @ViewChild('airQualityLabel', { static: false }) airQualityLabelTemplate!: TemplateRef<unknown>;
  @ViewChild('weatherLabel', { static: false }) weatherLabelTemplate!: TemplateRef<unknown>;
  @ViewChild('airQualityTab', { static: false }) airQualityTabTemplate!: TemplateRef<unknown>;
  @ViewChild('weatherTab', { static: false }) weatherTabTemplate!: TemplateRef<unknown>;

  tabs: TabItem[] = [];

  // Getters pour les FormControls (comme dans login)
  get citySearchControl(): FormControl {
    return this.filterForm.get('citySearchUtils') as FormControl;
  }

  get startDateControl(): FormControl {
    return this.filterForm.get('startDate') as FormControl;
  }

  get endDateControl(): FormControl {
    return this.filterForm.get('endDate') as FormControl;
  }

  ngOnInit() {
    // Créer le FormGroup
    this.filterForm = this.fb.group({
      citySearch: [''],
      startDate: [''],
      endDate: ['']
    });

    // Synchroniser FormControls avec signals
    this.citySearchControl.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(value => {
        this.citySearchInput.set(value || '');
        if (!this.isInitializing) {
          this.onCityInputChange(value || '');
        }
      });

    this.startDateControl.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(value => {
        this.startDate.set(value || '');
        if (value) {
          this.onDateChange();
        }
      });

    this.endDateControl.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(value => {
        this.endDate.set(value || '');
        if (value) {
          this.onDateChange();
        }
      });

    // Charger toutes les villes pour le dropdown
    this.cityService.getAll()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (cities) => {
          this.cities.set(cities);
          this.filteredCities.set(cities); // Toutes les villes par défaut

          // Essayer de récupérer le profil synchrone d'abord
          const profile = this.userService.currentUserProfile;

          if (profile?.user?.address?.city) {
            this.initializeWithUserCity(cities, profile.user.address.city.name);
          }

          // Écouter aussi les changements de profil
          this.userService.userProfile$
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe(newProfile => {
              if (newProfile?.user?.address?.city && !this.selectedCityName()) {
                this.initializeWithUserCity(cities, newProfile.user.address.city.name);
              }
            });
        },
        error: (err) => console.error('Erreur chargement villes:', err)
      });
  }

  ngAfterViewInit() {
    this.tabs = [
      {
        label: 'Qualité de l\'air',
        labelTemplate: this.airQualityLabelTemplate,
        template: this.airQualityTabTemplate,
        color: 'primary'
      },
      {
        label: 'Météo',
        labelTemplate: this.weatherLabelTemplate,
        template: this.weatherTabTemplate,
        color: 'secondary'
      }
    ];
  }

  private initializeWithUserCity(cities: City[], cityName: string) {
    // Trouver la ville complète dans la liste pour obtenir l'inseeCode
    const userCity = cities.find(c => c.name === cityName);

    if (userCity) {
      this.selectedCityName.set(userCity.name);
      this.citySearchInput.set(userCity.name);
      this.selectedInseeCode.set(userCity.inseeCode);

      // Période par défaut : 30 derniers jours
      const today = new Date();
      const thirtyDaysAgo = new Date(today);
      thirtyDaysAgo.setDate(today.getDate() - 30);

      const startDateStr = thirtyDaysAgo.toISOString().split('T')[0];
      const endDateStr = today.toISOString().split('T')[0];

      this.startDate.set(startDateStr);
      this.endDate.set(endDateStr);

      // Mettre à jour les FormControls immédiatement
      this.citySearchControl.setValue(userCity.name);
      this.startDateControl.setValue(startDateStr);
      this.endDateControl.setValue(endDateStr);

      // Debug: vérifier que les valeurs sont bien dans les controls
      console.log('🔍 FormControl values:', {
        city: this.citySearchControl.value,
        start: this.startDateControl.value,
        end: this.endDateControl.value
      });

      // Activer les événements après l'initialisation
      setTimeout(() => {
        this.isInitializing = false;
        console.log('✅ Initialization complete, loading history...');
        // Charger les données automatiquement au démarrage
        this.loadHistory();
      }, 100);
    }
  }

  onCityInputChange(value: string) {
    this.citySearchInput.set(value);

    // Filtrer après 3 lettres minimum
    if (value.length >= 3) {
      const filtered = this.cities().filter(city =>
        city.name.toLowerCase().includes(value.toLowerCase()) ||
        city.postalCode.includes(value)
      );
      this.filteredCities.set(filtered);
      this.showDropdown.set(filtered.length > 0);
    } else {
      // Moins de 3 lettres ne pas afficher le dropdown
      this.filteredCities.set([]);
      this.showDropdown.set(false);
    }
  }

  onCitySelect(cityName: string) {
    const selectedCity = this.cities().find(c => c.name === cityName);
    if (selectedCity) {
      this.selectedCityName.set(selectedCity.name);
      this.citySearchInput.set(selectedCity.name);
      this.citySearchControl.setValue(selectedCity.name, { emitEvent: false });
      this.selectedInseeCode.set(selectedCity.inseeCode);
      this.showDropdown.set(false);
      this.loadHistory();
    }
  }

  onBlur() {
    setTimeout(() => this.showDropdown.set(false), 200);
  }

  onDateChange() {
    this.loadHistory();
  }

  onTabChange(index: number) {
    this.activeTab.set(index === 0 ? 'air-quality' : 'weather');
  }

  loadHistory() {
    const city = this.selectedCityName();
    if (!city) {
      return;
    }

    this.isLoading.set(true);
    this.error.set(null);

    this.orchestrator.loadCityHistoryTable(city)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (data) => {
          this.historyData.set(data);
          this.isLoading.set(false);
        },
        error: (err) => {
          this.error.set('Impossible de charger l\'historique');
          this.isLoading.set(false);
        }
      });
  }

  exportCSV() {
    const inseeCode = this.selectedInseeCode();
    const dateDebut = this.startDate();
    const dateFin = this.endDate();
    const type = this.activeTab();

    const params = new URLSearchParams();
    if (inseeCode) params.append('inseeCode', inseeCode);
    if (dateDebut) params.append('dateDebut', dateDebut);
    if (dateFin) params.append('dateFin', dateFin);
    params.append('type', type);

    const url = `${this.apiConfig.apiUrl}/export/csv?${params.toString()}`;
    window.open(url, '_blank');
  }

  exportPDF() {
    const inseeCode = this.selectedInseeCode();
    const dateDebut = this.startDate();
    const dateFin = this.endDate();
    const type = this.activeTab();

    const params = new URLSearchParams();
    if (inseeCode) params.append('inseeCode', inseeCode);
    if (dateDebut) params.append('dateDebut', dateDebut);
    if (dateFin) params.append('dateFin', dateFin);
    params.append('type', type);

    const url = `${this.apiConfig.apiUrl}/export/pdf?${params.toString()}`;
    window.open(url, '_blank');
  }
}
