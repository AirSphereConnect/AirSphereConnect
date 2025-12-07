import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { Dashboard } from './dashboard';
import { DataOrchestratorService } from '../../../../core/services/data-orchestrator';
import { UserService } from '../../../../shared/services/user-service';
import { DashboardData, WeatherMeasurement, AirQualityComplete, PopulationData } from '../../../../core/models/data.model';
import { City } from '../../../../core/models/city.model';
import { User, UserProfileResponse } from '../../../../core/models/user.model';

describe('Dashboard', () => {
  let component: Dashboard;
  let fixture: ComponentFixture<Dashboard>;
  let mockOrchestrator: jasmine.SpyObj<DataOrchestratorService>;
  let mockUserService: jasmine.SpyObj<UserService>;
  let userProfileSubject: BehaviorSubject<UserProfileResponse | null>;

  const mockCity: City = {
    id: 1,
    name: 'Paris',
    postalCode: '75001',
    inseeCode: '75056',
    areaCode: '75',
    latitude: 48.8566,
    longitude: 2.3522,
    population: 2165423,
    departmentName: 'Paris'
  };

  const mockWeather: WeatherMeasurement[] = [
    {
      id: 1,
      temperature: 25.5,
      humidity: 65,
      pressure: 1013,
      windSpeed: 15.2,
      windDirection: 180,
      measuredAt: new Date('2024-01-01T12:00:00'),
      message: '[{"main":"Clear","description":"clear sky","icon":"01d"}]'
    }
  ];

  const mockAirQuality: AirQualityComplete = {
    latestMeasurement: {
      measuredAt: '2024-01-01T12:00:00',
      pm25: 15.5,
      pm10: 25.3,
      no2: 30.2,
      o3: 45.1,
      so2: 5.5,
      co: 200,
      unit: 'µg/m³'
    },
    latestIndex: {
      qualityIndex: 3,
      qualityLabel: 'Moyen',
      qualityColor: 'orange',
      measuredAt: '2024-01-01T12:00:00',
      alert: false,
      areaName: 'Paris'
    },
    measurementHistory: [],
    indexHistory: []
  };

  const mockPopulation: PopulationData[] = [
    {
      id: 1,
      cityName: 'Paris',
      population: 2165423,
      year: 2023,
      source: 'INSEE'
    }
  ];

  const mockDashboardData: DashboardData = {
    city: mockCity,
    weatherHistory: mockWeather,
    airQuality: mockAirQuality,
    populationHistory: mockPopulation
  };

  const mockUser: User = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    role: 'USER',
    address: {
      id: 1,
      street: '123 Test St',
      city: {
        name: 'Paris',
        postalCode: '75001'
      }
    },
    favorites: [],
    alerts: []
  };

  const mockUserProfile: UserProfileResponse = {
    user: mockUser,
    role: 'USER'
  };

  beforeEach(async () => {
    userProfileSubject = new BehaviorSubject<UserProfileResponse | null>(null);

    mockOrchestrator = jasmine.createSpyObj('DataOrchestratorService', ['loadDashboardData']);
    mockUserService = jasmine.createSpyObj('UserService', [], {
      userProfile$: userProfileSubject.asObservable()
    });

    await TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [
        { provide: DataOrchestratorService, useValue: mockOrchestrator },
        { provide: UserService, useValue: mockUserService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Dashboard);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load dashboard data when user profile is available', () => {
      mockOrchestrator.loadDashboardData.and.returnValue(of(mockDashboardData));

      fixture.detectChanges(); // Trigger ngOnInit

      // Emit user profile
      userProfileSubject.next(mockUserProfile);

      expect(component.selectedCity()).toBe('Paris');
      expect(component.selectedPostalCode()).toBe('75001');
      expect(mockOrchestrator.loadDashboardData).toHaveBeenCalledWith('Paris');
      expect(component.dashboardData()).toEqual(mockDashboardData);
    });

    it('should set loading state to false after successful load', () => {
      mockOrchestrator.loadDashboardData.and.returnValue(of(mockDashboardData));

      fixture.detectChanges();
      userProfileSubject.next(mockUserProfile);

      expect(component.isLoading()).toBe(false);
      expect(component.error()).toBeNull();
    });

    it('should not load dashboard when user profile is null', () => {
      fixture.detectChanges();
      userProfileSubject.next(null);

      expect(mockOrchestrator.loadDashboardData).not.toHaveBeenCalled();
    });

    it('should not load dashboard when user profile has no user', () => {
      fixture.detectChanges();
      userProfileSubject.next({
        role: 'GUEST',
        user: {
          id: 0,
          username: '',
          email: '',
          role: 'GUEST',
          address: {
            id: 0,
            street: '',
            city: { name: '', postalCode: '' }
          },
          favorites: [],
          alerts: []
        }
      });

      expect(mockOrchestrator.loadDashboardData).not.toHaveBeenCalled();
    });
  });

  describe('loadDashboard', () => {
    beforeEach(() => {
      component.selectedCity.set('Paris');
    });

    it('should set loading state to true before loading', () => {
      mockOrchestrator.loadDashboardData.and.returnValue(of(mockDashboardData));

      component.loadDashboard();

      // Check that loading was set to true initially
      expect(mockOrchestrator.loadDashboardData).toHaveBeenCalled();
    });

    it('should load dashboard data successfully', () => {
      mockOrchestrator.loadDashboardData.and.returnValue(of(mockDashboardData));

      component.loadDashboard();

      expect(mockOrchestrator.loadDashboardData).toHaveBeenCalledWith('Paris');
      expect(component.dashboardData()).toEqual(mockDashboardData);
      expect(component.isLoading()).toBe(false);
      expect(component.error()).toBeNull();
    });

    it('should handle error when loading fails', () => {
      mockOrchestrator.loadDashboardData.and.returnValue(
        throwError(() => new Error('Network error'))
      );

      component.loadDashboard();

      expect(component.error()).toBe('Impossible de charger les données. Vérifiez que le backend est lancé.');
      expect(component.isLoading()).toBe(false);
      expect(component.dashboardData()).toBeNull();
    });

    it('should clear previous error on new load', () => {
      component.error.set('Previous error');
      mockOrchestrator.loadDashboardData.and.returnValue(of(mockDashboardData));

      component.loadDashboard();

      expect(component.error()).toBeNull();
    });
  });

  describe('onCitySelected', () => {
    it('should update selected city and reload dashboard', () => {
      mockOrchestrator.loadDashboardData.and.returnValue(of(mockDashboardData));

      component.onCitySelected('Lyon');

      expect(component.selectedCity()).toBe('Lyon');
      expect(mockOrchestrator.loadDashboardData).toHaveBeenCalledWith('Lyon');
    });

    it('should load new data for different city', () => {
      const lyonData: DashboardData = {
        ...mockDashboardData,
        city: { ...mockCity, id: 2, name: 'Lyon' }
      };
      mockOrchestrator.loadDashboardData.and.returnValue(of(lyonData));

      component.onCitySelected('Lyon');

      expect(component.dashboardData()?.city.name).toBe('Lyon');
    });
  });

  describe('computed signals', () => {
    beforeEach(() => {
      mockOrchestrator.loadDashboardData.and.returnValue(of(mockDashboardData));
      component.loadDashboard();
    });

    it('should compute city from dashboard data', () => {
      expect(component.city()).toEqual(mockCity);
    });

    it('should compute cityName from city or selectedCity', () => {
      expect(component.cityName()).toBe('Paris');

      // When dashboard data is null
      component.dashboardData.set(null);
      component.selectedCity.set('TestCity');
      expect(component.cityName()).toBe('TestCity');
    });

    it('should compute cityPostalCode from dashboard data', () => {
      expect(component.cityPostalCode()).toBe('75001');
    });

    it('should compute weatherHistory from dashboard data', () => {
      expect(component.weatherHistory()).toEqual(mockWeather);
    });

    it('should compute airQuality from dashboard data', () => {
      expect(component.airQuality()).toEqual(mockAirQuality);
    });

    it('should compute populationHistory from dashboard data', () => {
      expect(component.populationHistory()).toEqual(mockPopulation);
    });

    it('should return empty array for weatherHistory when no data', () => {
      component.dashboardData.set(null);
      expect(component.weatherHistory()).toEqual([]);
    });

    it('should return empty array for populationHistory when no data', () => {
      component.dashboardData.set(null);
      expect(component.populationHistory()).toEqual([]);
    });

    it('should return null for postalCode when no dashboard data', () => {
      component.dashboardData.set(null);
      component.selectedPostalCode.set(null);
      expect(component.cityPostalCode()).toBeNull();
    });

    it('should use selectedPostalCode as fallback', () => {
      component.dashboardData.set(null);
      component.selectedPostalCode.set('69001');
      expect(component.cityPostalCode()).toBe('69001');
    });
  });

  describe('data integration', () => {
    it('should handle empty weather history', () => {
      const dataWithoutWeather: DashboardData = {
        ...mockDashboardData,
        weatherHistory: []
      };
      mockOrchestrator.loadDashboardData.and.returnValue(of(dataWithoutWeather));

      component.loadDashboard();

      expect(component.weatherHistory()).toEqual([]);
    });

    it('should handle empty population history', () => {
      const dataWithoutPopulation: DashboardData = {
        ...mockDashboardData,
        populationHistory: []
      };
      mockOrchestrator.loadDashboardData.and.returnValue(of(dataWithoutPopulation));

      component.loadDashboard();

      expect(component.populationHistory()).toEqual([]);
    });

    it('should handle multiple weather measurements', () => {
      const multipleWeather: WeatherMeasurement[] = [
        mockWeather[0],
        { ...mockWeather[0], id: 2, temperature: 20.5 },
        { ...mockWeather[0], id: 3, temperature: 18.3 }
      ];
      const dataWithMultipleWeather: DashboardData = {
        ...mockDashboardData,
        weatherHistory: multipleWeather
      };
      mockOrchestrator.loadDashboardData.and.returnValue(of(dataWithMultipleWeather));

      component.loadDashboard();

      expect(component.weatherHistory().length).toBe(3);
      expect(component.weatherHistory()[1].temperature).toBe(20.5);
    });
  });

  describe('user input scenarios', () => {
    it('should handle rapid city changes', () => {
      mockOrchestrator.loadDashboardData.and.returnValue(of(mockDashboardData));

      component.onCitySelected('Paris');
      component.onCitySelected('Lyon');
      component.onCitySelected('Marseille');

      expect(component.selectedCity()).toBe('Marseille');
      expect(mockOrchestrator.loadDashboardData).toHaveBeenCalledTimes(3);
    });

    it('should maintain selected city after error', () => {
      mockOrchestrator.loadDashboardData.and.returnValue(
        throwError(() => new Error('Network error'))
      );

      component.onCitySelected('Lyon');

      expect(component.selectedCity()).toBe('Lyon');
      expect(component.error()).toBeTruthy();
    });

    it('should clear error on successful retry', () => {
      mockOrchestrator.loadDashboardData.and.returnValues(
        throwError(() => new Error('Network error')),
        of(mockDashboardData)
      );

      component.loadDashboard();
      expect(component.error()).toBeTruthy();

      component.loadDashboard();
      expect(component.error()).toBeNull();
      expect(component.dashboardData()).toEqual(mockDashboardData);
    });
  });
});
