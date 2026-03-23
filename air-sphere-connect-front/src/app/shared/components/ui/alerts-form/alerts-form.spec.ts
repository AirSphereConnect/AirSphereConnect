import { ComponentFixture, TestBed, fakeAsync, flush } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { of, throwError, Subject } from 'rxjs';
import { SimpleChange } from '@angular/core';

import { AlertsForm } from './alerts-form';
import { AlertsService } from '../../../services/alerts-service';
import { CityService } from '../../../../core/services/city';
import { UserService } from '../../../services/user-service';
import { NotificationService } from '../../../services/notification-service';
import {City} from '../../../../core/models/city.model';

describe('AlertsForm', () => {
  let component: AlertsForm;
  let fixture: ComponentFixture<AlertsForm>;
  let alertsService: jasmine.SpyObj<AlertsService>;
  let cityService: jasmine.SpyObj<CityService>;
  let userService: jasmine.SpyObj<UserService>;
  let notificationService: jasmine.SpyObj<NotificationService>;

  const mockCities: City[] = [
    { id: 1, name: 'Montpellier', postalCode:'34000', inseeCode: '34172', areaCode: '243400017', latitude: 43.625, longitude: 3.876, population: 516657, departmentName: 'Herault'},
    { id: 2, name: 'Nîmes', postalCode:'30000', inseeCode: '30189', areaCode: '243000643', latitude: 43.836, longitude: 4.360, population: 150444, departmentName: 'Gard'},

  ];

  beforeEach(async () => {
    const alertsServiceSpy = jasmine.createSpyObj('AlertsService', ['addAlerts', 'editAlerts', 'deleteAlerts']);
    const cityServiceSpy = jasmine.createSpyObj('CityService', ['searchCities']);
    const userServiceSpy = jasmine.createSpyObj('UserService', ['fetchUserProfile']);
    const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);

    await TestBed.configureTestingModule({
      imports: [AlertsForm],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: AlertsService, useValue: alertsServiceSpy },
        { provide: CityService, useValue: cityServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlertsForm);
    component = fixture.componentInstance;
    alertsService = TestBed.inject(AlertsService) as jasmine.SpyObj<AlertsService>;
    cityService = TestBed.inject(CityService) as jasmine.SpyObj<CityService>;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form initialization', () => {
    it('should initialize form with default values', () => {
      expect(component.alertsForm).toBeDefined();
      expect(component.alertsForm.get('activeAlert')?.value).toBeFalse();
      expect(component.alertsForm.get('cityName')?.value).toBe('');
    });

    it('should have required validators on fields', () => {
      const activeAlertControl = component.alertsForm.get('activeAlert');
      const cityNameControl = component.alertsForm.get('cityName');

      activeAlertControl?.setValue(null);
      cityNameControl?.setValue('');

      expect(activeAlertControl?.hasError('required')).toBeTrue();
      expect(cityNameControl?.hasError('required')).toBeTrue();
    });

    it('should initialize cityIdSelected to null', () => {
      expect(component.cityIdSelected).toBeNull();
    });

    it('should initialize isDeleteMode to false', () => {
      expect(component.isDeleteMode).toBeFalse();
    });
  });

  describe('ngOnChanges', () => {
    it('should patch form with initialAlertsData when provided', () => {
      const alertData = {
        cityId: 1,
        cityName: 'Paris',
        enabled: true
      };

      component.initialAlertsData = alertData;
      component.ngOnChanges({
        initialAlertsData: new SimpleChange(null, alertData, false)
      });

      expect(component.alertsForm.get('activeAlert')?.value).toBeTrue();
      expect(component.alertsForm.get('cityName')?.value).toBe('Paris');
      expect(component.cityIdSelected).toBe(1);
      expect(component.isDeleteMode).toBeFalse();
    });

    it('should handle missing cityName', () => {
      const alertData = {
        cityId: 1,
        cityName: '',
        enabled: false
      };

      component.initialAlertsData = alertData;
      component.ngOnChanges({
        initialAlertsData: new SimpleChange(null, alertData, false)
      });

      expect(component.alertsForm.get('cityName')?.value).toBe('');
    });

    it('should not patch form when initialAlertsData is null', () => {
      component.alertsForm.get('activeAlert')?.setValue(true);
      component.ngOnChanges({});

      expect(component.alertsForm.get('activeAlert')?.value).toBeTrue();
    });
  });

  describe('City search and selection', () => {
    it('should update cityQuery when onCityInput is called', () => {
      const event = { target: { value: 'Paris' } } as any;
      component.onCityInput(event);
      expect(component.cityQuery()).toBe('Paris');
    });

    it('should select city and update form', () => {
      component.selectCity(mockCities[0]);

      expect(component.alertsForm.get('cityName')?.value).toBe('Montpellier');
      expect(component.cityIdSelected).toBe(1);
      expect(component.citySuggestions()).toEqual([]);
    });
  });

  describe('submitForm - Creating new alert', () => {
    beforeEach(() => {
      component.editingAlertsId = null;
      component.alertsForm.patchValue({
        activeAlert: true,
        cityName: 'Paris'
      });
      component.cityIdSelected = 1;
      component.alertsForm.markAsDirty();
    });

    it('should show error message if form is invalid', () => {
      component.alertsForm.get('cityName')?.setValue('');
      component.submitForm();

      expect(notificationService.showError).toHaveBeenCalledWith(
        'Veuillez modifier au moins un champ et sélectionner une ville.'
      );
      expect(alertsService.addAlerts).not.toHaveBeenCalled();
    });

    it('should show error message if cityId is not selected for new entry', () => {
      component.cityIdSelected = null;
      component.submitForm();

      expect(notificationService.showError).toHaveBeenCalledWith(
        'Veuillez modifier au moins un champ et sélectionner une ville.'
      );
    });

    it('should call addAlerts with correct parameters', () => {
      alertsService.addAlerts.and.returnValue(of(undefined));

      component.submitForm();

      expect(alertsService.addAlerts).toHaveBeenCalledWith({
        enabled: true,
        cityId: 1
      });
    });

    it('should set isLoading to true during submission', () => {
      const addSubject = new Subject<any>();
      alertsService.addAlerts.and.returnValue(addSubject.asObservable());
      component.submitForm();
      expect(component.isLoading()).toBeTrue();
      addSubject.next(undefined);
      addSubject.complete();
    });

    it('should handle successful alert creation', fakeAsync(() => {
      alertsService.addAlerts.and.returnValue(of(undefined));
      spyOn(component.submitSuccess, 'emit');

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(userService.fetchUserProfile).toHaveBeenCalled();
      expect(component.submitSuccess.emit).toHaveBeenCalled();
      expect(component.cityIdSelected).toBeNull();
      expect(component.isDeleteMode).toBeFalse();
    }));

    it('should handle error during alert creation', fakeAsync(() => {
      alertsService.addAlerts.and.returnValue(throwError(() => new Error('API Error')));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(notificationService.showError).toHaveBeenCalledWith("Erreur lors de l'enregistrement de l'alerte.");
    }));
  });

  describe('submitForm - Editing existing alert', () => {
    beforeEach(() => {
      component.editingAlertsId = 1;
      component.initialAlertsData = { cityId: 1, cityName: 'Paris', enabled: true };
      component.alertsForm.patchValue({
        activeAlert: false,
        cityName: 'Paris'
      });
      component.cityIdSelected = 1;
      component.alertsForm.markAsDirty();
    });

    it('should call editAlerts with correct parameters', () => {
      alertsService.editAlerts.and.returnValue(of(undefined));

      component.submitForm();

      expect(alertsService.editAlerts).toHaveBeenCalledWith(
        { enabled: false, cityId: 1 },
        1
      );
    });

    it('should handle successful alert update', fakeAsync(() => {
      alertsService.editAlerts.and.returnValue(of(undefined));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(userService.fetchUserProfile).toHaveBeenCalled();
    }));

    it('should handle error during alert update', fakeAsync(() => {
      alertsService.editAlerts.and.returnValue(throwError(() => new Error('API Error')));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(notificationService.showError).toHaveBeenCalledWith("Erreur lors de l'enregistrement de l'alerte.");
    }));
  });

  describe('submitForm - Deleting alert', () => {
    beforeEach(() => {
      component.editingAlertsId = 1;
      component.isDeleteMode = true;
      component.alertsForm.patchValue({
        activeAlert: true,
        cityName: 'Paris'
      });
      component.cityIdSelected = 1;
      component.alertsForm.markAsDirty();
    });

    it('should call deleteAlerts with correct id', () => {
      alertsService.deleteAlerts.and.returnValue(of(undefined));

      component.submitForm();

      expect(alertsService.deleteAlerts).toHaveBeenCalledWith(1);
    });

    it('should not call delete if editingAlertsId is null', () => {
      component.editingAlertsId = null;
      alertsService.deleteAlerts.and.returnValue(of(undefined));

      component.submitForm();

      expect(alertsService.deleteAlerts).not.toHaveBeenCalled();
    });

    it('should handle successful alert deletion', fakeAsync(() => {
      alertsService.deleteAlerts.and.returnValue(of(undefined));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(userService.fetchUserProfile).toHaveBeenCalled();
      expect(component.isDeleteMode).toBeFalse();
    }));

    it('should handle error during alert deletion', fakeAsync(() => {
      alertsService.deleteAlerts.and.returnValue(throwError(() => new Error('Delete failed')));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(notificationService.showError).toHaveBeenCalledWith("Erreur lors de la suppression de l'alerte.");
    }));
  });

  describe('onClose', () => {
    it('should reset form to default values', () => {
      component.alertsForm.patchValue({
        activeAlert: true,
        cityName: 'Paris'
      });

      component.onClose();

      expect(component.alertsForm.get('activeAlert')?.value).toBeFalse();
      expect(component.alertsForm.get('cityName')?.value).toBe('');
    });

    it('should reset cityIdSelected to null', () => {
      component.cityIdSelected = 1;
      component.onClose();
      expect(component.cityIdSelected).toBeNull();
    });

    it('should reset isDeleteMode to false', () => {
      component.isDeleteMode = true;
      component.onClose();
      expect(component.isDeleteMode).toBeFalse();
    });

    it('should set isOpen to false', () => {
      component.isOpen.set(true);
      component.onClose();
      expect(component.isOpen()).toBeFalse();
    });

    it('should emit closeModal event', () => {
      spyOn(component.closeModal, 'emit');
      component.onClose();
      expect(component.closeModal.emit).toHaveBeenCalled();
    });
  });

  describe('Signal reactivity', () => {
    it('should initialize isLoading signal to false', () => {
      expect(component.isLoading()).toBeFalse();
    });

    it('should initialize cityQuery signal to empty string', () => {
      expect(component.cityQuery()).toBe('');
    });

    it('should initialize citySuggestions signal to empty array', () => {
      expect(component.citySuggestions()).toEqual([]);
    });
  });

  describe('cleanup', () => {
    it('should be created and destroyed without errors', () => {
      expect(component).toBeTruthy();
    });
  });
});
