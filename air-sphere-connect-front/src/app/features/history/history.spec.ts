import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {of, BehaviorSubject, Observable} from 'rxjs';
import {History} from './history';
import {UserService} from '../../shared/services/user-service';
import {CityService} from '../../core/services/city';
import {DataOrchestratorService} from '../../core/services/data-orchestrator';
import {ApiConfigService} from '../../core/services/api';
import {City, CityHistoryData, CityDailySnapshot} from '../../core/models/city.model';
import {User, UserProfileResponse} from '../../core/models/user.model';

describe('History', () => {
  let component: History;
  let fixture: ComponentFixture<History>;
  let mockUserService: jasmine.SpyObj<UserService>;
  let mockCityService: jasmine.SpyObj<CityService>;
  let mockOrchestrator: jasmine.SpyObj<DataOrchestratorService>;
  let mockApiConfig: jasmine.SpyObj<ApiConfigService>;
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

  const mockCities: City[] = [
    mockCity,
    {
      id: 2,
      name: 'Lyon',
      postalCode: '69001',
      inseeCode: '69123',
      areaCode: '69',
      latitude: 45.764,
      longitude: 4.8357,
      population: 516092,
      departmentName: 'Rhône'
    },
    {
      id: 3,
      name: 'Marseille',
      postalCode: '13001',
      inseeCode: '13055',
      areaCode: '13',
      latitude: 43.2965,
      longitude: 5.3698,
      population: 869815,
      departmentName: 'Bouches-du-Rhône'
    }
  ];

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

  const mockHistoryData: CityHistoryData = {
    city: mockCity,
    dailySnapshots: []
  };

  beforeEach(async () => {
    userProfileSubject = new BehaviorSubject<UserProfileResponse | null>(null);

    mockUserService = jasmine.createSpyObj('UserService', [], {
      userProfile$: userProfileSubject.asObservable(),
      currentUserProfile: null
    });

    mockCityService = jasmine.createSpyObj('CityService', ['getAll']);
    mockOrchestrator = jasmine.createSpyObj('DataOrchestratorService', ['loadCityHistoryTable']);
    mockApiConfig = jasmine.createSpyObj('ApiConfigService', [], {
      apiUrl: 'http://localhost:8080/api'
    });

    await TestBed.configureTestingModule({
      imports: [History],
      providers: [
        {provide: UserService, useValue: mockUserService},
        {provide: CityService, useValue: mockCityService},
        {provide: DataOrchestratorService, useValue: mockOrchestrator},
        {provide: ApiConfigService, useValue: mockApiConfig}
      ]
    }).compileComponents();

    mockCityService.getAll.and.returnValue(of(mockCities));
    mockOrchestrator.loadCityHistoryTable.and.returnValue(of(mockHistoryData));

    fixture = TestBed.createComponent(History);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should initialize filterForm with empty values', () => {
      fixture.detectChanges();

      expect(component.filterForm).toBeDefined();
      expect(component.citySearchControl.value).toBe('');
      expect(component.startDateControl.value).toBe('');
      expect(component.endDateControl.value).toBe('');
    });

    it('should load all cities', () => {
      fixture.detectChanges();

      expect(mockCityService.getAll).toHaveBeenCalled();
      expect(component.cities().length).toBe(3);
      expect(component.filteredCities().length).toBe(3);
    });

    it('should initialize with user city from profile', fakeAsync(() => {
      Object.defineProperty(mockUserService, 'currentUserProfile', {
        get: () => mockUserProfile,
        configurable: true
      });

      fixture.detectChanges();
      tick();

      expect(component.selectedCityName()).toBe('Paris');
      expect(component.citySearchInput()).toBe('Paris');
      expect(component.selectedInseeCode()).toBe('75056');
      expect(component.startDate()).toBeTruthy();
      expect(component.endDate()).toBeTruthy();
    }));

    it('should set default date range to last 30 days', fakeAsync(() => {
      Object.defineProperty(mockUserService, 'currentUserProfile', {
        get: () => mockUserProfile,
        configurable: true
      });

      fixture.detectChanges();
      tick();

      const startDate = new Date(component.startDate());
      const endDate = new Date(component.endDate());
      const daysDiff = Math.floor((endDate.getTime() - startDate.getTime()) / (1000*60*60*24));
      expect(daysDiff).toBeCloseTo(30, 1);
    }));

    it('should sync form controls with signals', () => {
      fixture.detectChanges();

      component.citySearchControl.setValue('Lyon');

      expect(component.citySearchInput()).toBe('Lyon');
    });

    it('should listen to userProfile$ changes', (done) => {
      fixture.detectChanges();

      setTimeout(() => {
        userProfileSubject.next(mockUserProfile);

        setTimeout(() => {
          expect(component.selectedCityName()).toBe('Paris');
          done();
        }, 200);
      }, 100);
    });
  });

  describe('city search and filtering', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should filter cities when input has 3+ characters', () => {
      component.onCityInputChange('Par');

      expect(component.filteredCities().length).toBe(1);
      expect(component.filteredCities()[0].name).toBe('Paris');
      expect(component.showDropdown()).toBe(true);
    });

    it('should filter by postal code', () => {
      component.onCityInputChange('690');

      expect(component.filteredCities().length).toBe(1);
      expect(component.filteredCities()[0].name).toBe('Lyon');
    });

    it('should not show dropdown with less than 3 characters', () => {
      component.onCityInputChange('Pa');

      expect(component.filteredCities().length).toBe(0);
      expect(component.showDropdown()).toBe(false);
    });

    it('should be case insensitive', () => {
      component.onCityInputChange('lyon');

      expect(component.filteredCities().length).toBe(1);
      expect(component.filteredCities()[0].name).toBe('Lyon');
    });

    it('should show dropdown when matches found', () => {
      component.onCityInputChange('Mar');

      expect(component.showDropdown()).toBe(true);
    });

    it('should hide dropdown when no matches', () => {
      component.onCityInputChange('XYZ');

      expect(component.showDropdown()).toBe(false);
    });
  });

  describe('city selection', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should select city and load history', () => {
      component.onCitySelect('Lyon');

      expect(component.selectedCityName()).toBe('Lyon');
      expect(component.citySearchInput()).toBe('Lyon');
      expect(component.selectedInseeCode()).toBe('69123');
      expect(component.showDropdown()).toBe(false);
      expect(mockOrchestrator.loadCityHistoryTable).toHaveBeenCalledWith('Lyon');
    });

    it('should update form control without emitting event', () => {
      spyOn(component.citySearchControl, 'setValue');

      component.onCitySelect('Lyon');

      expect(component.citySearchControl.setValue).toHaveBeenCalledWith('Lyon', {emitEvent: false});
    });

    it('should handle unknown city gracefully', () => {
      component.onCitySelect('UnknownCity');

      expect(component.selectedCityName()).toBe('');
    });
  });

  describe('date changes', () => {
    beforeEach(() => {
      fixture.detectChanges();
      component.selectedCityName.set('Paris');
    });

    it('should reload history when start date changes', () => {
      mockOrchestrator.loadCityHistoryTable.calls.reset();

      component.startDateControl.setValue('2024-01-01');

      expect(component.startDate()).toBe('2024-01-01');
      expect(mockOrchestrator.loadCityHistoryTable).toHaveBeenCalled();
    });

    it('should reload history when end date changes', () => {
      mockOrchestrator.loadCityHistoryTable.calls.reset();

      component.endDateControl.setValue('2024-01-31');

      expect(component.endDate()).toBe('2024-01-31');
      expect(mockOrchestrator.loadCityHistoryTable).toHaveBeenCalled();
    });
  });

  describe('loadHistory', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should not load if no city selected', () => {
      component.selectedCityName.set('');
      component.loadHistory();

      expect(mockOrchestrator.loadCityHistoryTable).not.toHaveBeenCalled();
    });

    it('should set loading state', () => {
      component.selectedCityName.set('Paris');
      component.loadHistory();

      expect(component.isLoading()).toBe(false);
      expect(component.error()).toBeNull();
    });

    it('should load history data successfully', (done) => {
      component.selectedCityName.set('Paris');
      component.loadHistory();

      setTimeout(() => {
        expect(component.historyData()).toEqual(mockHistoryData);
        expect(component.isLoading()).toBe(false);
        expect(component.error()).toBeNull();
        done();
      }, 100);
    });

    it('should handle error when loading fails', (done) => {
      mockOrchestrator.loadCityHistoryTable.and.returnValue(
        new Observable<CityHistoryData>(observer => {
          setTimeout(() => observer.error(new Error('Network error')), 10);
        })
      );
      mockOrchestrator.loadCityHistoryTable.and.returnValue(
        new Observable<CityHistoryData>(observer => {
          setTimeout(() => observer.error(new Error('Network error')), 10);
        })
      );

      component.selectedCityName.set('Paris');
      component.loadHistory();

      setTimeout(() => {
        expect(component.error()).toBe('Impossible de charger l\'historique');
        expect(component.isLoading()).toBe(false);
        done();
      }, 100);
    });
  });

  describe('tab management', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should start with air-quality tab active', () => {
      expect(component.activeTab()).toBe('air-quality');
    });

    it('should change to weather tab', () => {
      component.onTabChange(1);

      expect(component.activeTab()).toBe('weather');
    });

    it('should change to air-quality tab', () => {
      component.activeTab.set('weather');
      component.onTabChange(0);

      expect(component.activeTab()).toBe('air-quality');
    });
  });

  describe('export functions', () => {
    beforeEach(() => {
      fixture.detectChanges();
      component.selectedInseeCode.set('75056');
      component.startDate.set('2024-01-01');
      component.endDate.set('2024-01-31');
      spyOn(window, 'open');
    });

    it('should export CSV with correct parameters', () => {
      component.activeTab.set('air-quality');
      component.exportCSV();

      const expectedUrl = 'http://localhost:8080/api/export/csv?inseeCode=75056&dateDebut=2024-01-01&dateFin=2024-01-31&type=air-quality';
      expect(window.open).toHaveBeenCalledWith(expectedUrl, '_blank');
    });

    it('should export PDF with correct parameters', () => {
      component.activeTab.set('weather');
      component.exportPDF();

      const expectedUrl = 'http://localhost:8080/api/export/pdf?inseeCode=75056&dateDebut=2024-01-01&dateFin=2024-01-31&type=weather';
      expect(window.open).toHaveBeenCalledWith(expectedUrl, '_blank');
    });

    it('should include active tab type in export', () => {
      component.activeTab.set('air-quality');
      component.exportCSV();

      expect(window.open).toHaveBeenCalledWith(jasmine.stringContaining('type=air-quality'), '_blank');
    });

    it('should handle missing optional parameters', () => {
      component.selectedInseeCode.set('');
      component.startDate.set('');
      component.endDate.set('');

      component.exportCSV();

      const expectedUrl = 'http://localhost:8080/api/export/csv?type=air-quality';
      expect(window.open).toHaveBeenCalledWith(expectedUrl, '_blank');
    });
  });

  describe('onBlur', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should hide dropdown after delay', (done) => {
      component.showDropdown.set(true);
      component.onBlur();

      setTimeout(() => {
        expect(component.showDropdown()).toBe(false);
        done();
      }, 250);
    });
  });

  describe('form control getters', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should return citySearch control', () => {
      const control = component.citySearchControl;

      expect(control).toBeDefined();
      expect(control.value).toBe('');
    });

    it('should return startDate control', () => {
      const control = component.startDateControl;

      expect(control).toBeDefined();
      expect(control.value).toBe('');
    });

    it('should return endDate control', () => {
      const control = component.endDateControl;

      expect(control).toBeDefined();
      expect(control.value).toBe('');
    });
  });
});
