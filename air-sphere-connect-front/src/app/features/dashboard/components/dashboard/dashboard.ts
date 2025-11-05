import {Component, computed, DestroyRef, Input, OnInit, signal} from '@angular/core';
import { TemperatureChart } from '../temperature-chart/temperature-chart';
import { CityPopulationChart } from '../city-population-chart/city-population-chart';
import { PollutantsChart } from '../pollutants-chart/pollutants-chart';
import { Map } from '../map/map';
import {FormsModule} from '@angular/forms';
import { inject } from '@angular/core';
import { DataOrchestratorService } from '../../../../core/services/data-orchestrator';
import { DashboardData } from '../../../../core/models/data.model';
import {UserService} from '../../../../shared/services/user-service';
import {Button} from '../../../../shared/components/ui/button/button';
import {User} from '../../../../core/models/user.model';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    TemperatureChart,
    CityPopulationChart,
    PollutantsChart,
    Map,
    FormsModule,
    Button
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})

export class Dashboard implements OnInit {
  private orchestrator = inject(DataOrchestratorService);
  private userService = inject(UserService);
  private destroyRef = inject(DestroyRef);

  selectedCity = signal('');
  selectedPostalCode = signal<string | null>(null);

  dashboardData = signal<DashboardData | null>(null);
  isLoading = signal(false);
  error = signal<string | null>(null);

  city = computed(() => this.dashboardData()?.city);
  cityName = computed(() => this.city()?.name || this.selectedCity());
  cityPostalCode = computed(() =>
    this.dashboardData()?.city?.postalCode ?? this.selectedPostalCode()
  );
  weatherHistory = computed(() => this.dashboardData()?.weatherHistory || []);
  airQuality = computed(() => {
    const aq = this.dashboardData()?.airQuality;
    console.log('🏭 Dashboard - airQuality computed:', aq);
    console.log('📈 Dashboard - measurementHistory:', aq?.measurementHistory);
    return aq;
  });
  populationHistory = computed(() => this.dashboardData()?.populationHistory || []);
  @Input() user: User | null = null;

  ngOnInit() {
    // Récupération de la ville par défaut depuis le profil utilisateur
    this.userService.userProfile$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(profile => {
        if (profile?.user) {
          const cityName = profile.user.address.city.name;
          const postalCode = profile.user.address.city.postalCode;

          this.selectedCity.set(cityName);
          this.selectedPostalCode.set(postalCode);
          this.loadDashboard();
        }
      });
  }

  loadDashboard() {
    this.isLoading.set(true);
    this.error.set(null);

    this.orchestrator.loadDashboardData(this.selectedCity()).subscribe({
      next: (data) => {
        console.log('✅ Dashboard data loaded:', data);
        this.dashboardData.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('❌ Error loading dashboard:', err);
        this.error.set('Impossible de charger les données. Vérifiez que le backend est lancé.');
        this.isLoading.set(false);
      }
    });
  }

  onCitySelected(cityName: string) {
    console.log('🏙️ Ville sélectionnée:', cityName);
    this.selectedCity.set(cityName);
    this.loadDashboard();

    // Scroll vers les graphiques
    setTimeout(() => {
      document.querySelector('.charts-grid')?.scrollIntoView({
        behavior: 'smooth'
      });
    }, 300);
  }
}
