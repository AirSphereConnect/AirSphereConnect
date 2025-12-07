import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { of, throwError, Subject } from 'rxjs';
import { SimpleChange } from '@angular/core';

import { AddressForm } from './address-form';
import { UserService } from '../../../services/user-service';
import { CityService } from '../../../../core/services/city';
import { NotificationService } from '../../../services/notification-service';

describe('AddressForm', () => {
  let component: AddressForm;
  let fixture: ComponentFixture<AddressForm>;
  let userService: jasmine.SpyObj<UserService>;
  let cityService: jasmine.SpyObj<CityService>;
  let notificationService: jasmine.SpyObj<NotificationService>;

  const mockCities = [
    { id: 1, name: 'Paris', lat: 48.8566, lon: 2.3522 },
    { id: 2, name: 'Lyon', lat: 45.7640, lon: 4.8357 },
    { id: 3, name: 'Marseille', lat: 43.2965, lon: 5.3698 }
  ];

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', ['editAddress']);
    const cityServiceSpy = jasmine.createSpyObj('CityService', ['searchCities']);
    const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);

    await TestBed.configureTestingModule({
      imports: [AddressForm],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: UserService, useValue: userServiceSpy },
        { provide: CityService, useValue: cityServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddressForm);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    cityService = TestBed.inject(CityService) as jasmine.SpyObj<CityService>;
    notificationService = TestBed.inject(NotificationService) as jasmine.SpyObj<NotificationService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form initialization', () => {
    it('should initialize form with empty fields', () => {
      expect(component.addressForm).toBeDefined();
      expect(component.addressForm.get('street')?.value).toBe('');
      expect(component.addressForm.get('cityName')?.value).toBe('');
      expect(component.addressForm.get('cityId')?.value).toBeNull();
    });

    it('should have required validators on all fields', () => {
      const streetControl = component.addressForm.get('street');
      const cityNameControl = component.addressForm.get('cityName');
      const cityIdControl = component.addressForm.get('cityId');

      streetControl?.setValue('');
      cityNameControl?.setValue('');
      cityIdControl?.setValue(null);

      expect(streetControl?.hasError('required')).toBeTrue();
      expect(cityNameControl?.hasError('required')).toBeTrue();
      expect(cityIdControl?.hasError('required')).toBeTrue();
    });

    it('should initialize cityQuery signal to empty string', () => {
      expect(component.cityQuery()).toBe('');
    });

    it('should initialize citySuggestions signal to empty array', () => {
      expect(component.citySuggestions()).toEqual([]);
    });

    it('should initialize selectedCityId to null', () => {
      expect(component.selectedCityId).toBeNull();
    });
  });

  describe('ngOnChanges', () => {
    it('should patch form with addressData when provided', () => {
      const addressData = {
        id: 1,
        street: '123 Rue de la Paix',
        city: { id: 1, name: 'Paris' }
      };

      component.addressData = addressData;
      component.ngOnChanges({
        addressData: new SimpleChange(null, addressData, false)
      });

      expect(component.addressForm.get('street')?.value).toBe('123 Rue de la Paix');
      expect(component.addressForm.get('cityName')?.value).toBe('Paris');
      expect(component.addressForm.get('cityId')?.value).toBe(1);
      expect(component.selectedCityId).toBe(1);
    });

    it('should handle missing city data', () => {
      const addressData = {
        id: 1,
        street: '123 Rue de la Paix',
        city: null
      };

      component.addressData = addressData;
      component.ngOnChanges({
        addressData: new SimpleChange(null, addressData, false)
      });

      expect(component.addressForm.get('street')?.value).toBe('123 Rue de la Paix');
      expect(component.addressForm.get('cityName')?.value).toBe('');
      expect(component.addressForm.get('cityId')?.value).toBeNull();
      expect(component.selectedCityId).toBeNull();
    });

    it('should not patch form when addressData is null', () => {
      component.addressForm.get('street')?.setValue('Current Street');
      component.ngOnChanges({});

      expect(component.addressForm.get('street')?.value).toBe('Current Street');
    });
  });

  describe('City search', () => {
    it('should update cityQuery when onCityInput is called', () => {
      const event = { target: { value: 'Paris' } };
      component.onCityInput(event);
      expect(component.cityQuery()).toBe('Paris');
    });

    it('should handle empty city input', () => {
      const event = { target: { value: '' } };
      component.onCityInput(event);
      expect(component.cityQuery()).toBe('');
    });
  });

  describe('City selection', () => {
    it('should update form and clear suggestions when city is selected', () => {
      component.citySuggestions.set([mockCities[0], mockCities[1]]);

      component.selectCity(mockCities[0]);

      expect(component.addressForm.get('cityName')?.value).toBe('Paris');
      expect(component.addressForm.get('cityId')?.value).toBe(1);
      expect(component.selectedCityId).toBe(1);
      expect(component.citySuggestions()).toEqual([]);
    });

    it('should update selectedCityId when different city is selected', () => {
      component.selectCity(mockCities[1]);
      expect(component.selectedCityId).toBe(2);
      expect(component.addressForm.get('cityName')?.value).toBe('Lyon');
    });
  });

  describe('Form validation', () => {
    it('should be invalid when street is empty', () => {
      component.addressForm.patchValue({
        street: '',
        cityName: 'Paris',
        cityId: 1
      });
      expect(component.addressForm.valid).toBeFalse();
    });

    it('should be invalid when cityName is empty', () => {
      component.addressForm.patchValue({
        street: '123 Rue de la Paix',
        cityName: '',
        cityId: 1
      });
      expect(component.addressForm.valid).toBeFalse();
    });

    it('should be invalid when cityId is null', () => {
      component.addressForm.patchValue({
        street: '123 Rue de la Paix',
        cityName: 'Paris',
        cityId: null
      });
      expect(component.addressForm.valid).toBeFalse();
    });

    it('should be valid when all fields are filled', () => {
      component.addressForm.patchValue({
        street: '123 Rue de la Paix',
        cityName: 'Paris',
        cityId: 1
      });
      expect(component.addressForm.valid).toBeTrue();
    });
  });

  describe('submit', () => {
    beforeEach(() => {
      component.addressData = { id: 1, street: 'Old Street', city: { id: 1, name: 'Paris' } };
      component.addressForm.patchValue({
        street: '123 Rue Neuve',
        cityName: 'Lyon',
        cityId: 2
      });
      component.selectedCityId = 2;
      component.addressForm.markAsDirty();
    });

    it('should show error message if form is invalid', () => {
      component.addressForm.get('street')?.setValue('');
      component.submit();

      expect(notificationService.showError).toHaveBeenCalledWith(
        'Veuillez modifier au moins un champ et sélectionner une ville.'
      );
      expect(userService.editAddress).not.toHaveBeenCalled();
    });

    it('should show error message if form is not dirty', () => {
      component.addressForm.markAsPristine();
      component.submit();

      expect(notificationService.showError).toHaveBeenCalledWith(
        'Veuillez modifier au moins un champ et sélectionner une ville.'
      );
      expect(userService.editAddress).not.toHaveBeenCalled();
    });

    it('should show error message if selectedCityId is null for new entry', () => {
      component.editingUserId = undefined;
      component.selectedCityId = null;
      component.submit();

      expect(notificationService.showError).toHaveBeenCalledWith(
        'Veuillez modifier au moins un champ et sélectionner une ville.'
      );
    });

    it('should return early if selectedCityId is null', () => {
      component.selectedCityId = null;
      component.submit();

      expect(component.isLoading()).toBeFalse();
      expect(userService.editAddress).not.toHaveBeenCalled();
    });

    it('should call editAddress with correct parameters', () => {
      userService.editAddress.and.returnValue(of({}));

      component.submit();

      expect(userService.editAddress).toHaveBeenCalledWith(1, {
        street: '123 Rue Neuve',
        city: { id: 2 }
      });
    });

    it('should set isLoading to true during submission', () => {
      const editSubject = new Subject<any>();
      userService.editAddress.and.returnValue(editSubject.asObservable());

      component.submit();

      expect(component.isLoading()).toBeTrue();
      editSubject.next({});
      editSubject.complete();
    });

    it('should set isLoading to false after successful submission', (done) => {
      userService.editAddress.and.returnValue(of({}));

      component.submit();

      setTimeout(() => {
        expect(component.isLoading()).toBeFalse();
        done();
      });
    });

    it('should emit updated event after successful submission', (done) => {
      userService.editAddress.and.returnValue(of({}));
      spyOn(component.updated, 'emit');

      component.submit();

      setTimeout(() => {
        expect(component.updated.emit).toHaveBeenCalled();
        done();
      });
    });

    it('should emit closeModal event after successful submission', (done) => {
      userService.editAddress.and.returnValue(of({}));
      spyOn(component.closeModal, 'emit');

      component.submit();

      setTimeout(() => {
        expect(component.closeModal.emit).toHaveBeenCalled();
        done();
      });
    });

    it('should handle error and set error message', (done) => {
      userService.editAddress.and.returnValue(throwError(() => new Error('API Error')));

      component.submit();

      setTimeout(() => {
        expect(component.isLoading()).toBeFalse();
        expect(notificationService.showError).toHaveBeenCalledWith('Erreur lors de la mise à jour.');
        done();
      });
    });

    it('should not emit events on error', (done) => {
      userService.editAddress.and.returnValue(throwError(() => new Error('API Error')));
      spyOn(component.updated, 'emit');
      spyOn(component.closeModal, 'emit');

      component.submit();

      setTimeout(() => {
        expect(component.updated.emit).not.toHaveBeenCalled();
        expect(component.closeModal.emit).not.toHaveBeenCalled();
        done();
      });
    });
  });

  describe('onCloseModal', () => {
    it('should reset form', () => {
      component.addressForm.patchValue({
        street: '123 Rue de la Paix',
        cityName: 'Paris',
        cityId: 1
      });

      component.onCloseModal();

      expect(component.addressForm.get('street')?.value).toBeNull();
      expect(component.addressForm.get('cityName')?.value).toBeNull();
      expect(component.addressForm.get('cityId')?.value).toBeNull();
    });

    it('should reset selectedCityId to null', () => {
      component.selectedCityId = 1;
      component.onCloseModal();
      expect(component.selectedCityId).toBeNull();
    });

    it('should set isOpen to false', () => {
      component.isOpen.set(true);
      component.onCloseModal();
      expect(component.isOpen()).toBeFalse();
    });

    it('should emit closeModal event', () => {
      spyOn(component.closeModal, 'emit');
      component.onCloseModal();
      expect(component.closeModal.emit).toHaveBeenCalled();
    });
  });

  describe('Signal reactivity', () => {
    it('should initialize isLoading signal to false', () => {
      expect(component.isLoading()).toBeFalse();
    });
  });
});
