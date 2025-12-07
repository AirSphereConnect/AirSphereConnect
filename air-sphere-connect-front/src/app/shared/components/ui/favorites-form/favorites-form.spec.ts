import { ComponentFixture, TestBed, fakeAsync, flush } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { of, throwError, Subject } from 'rxjs';
import { SimpleChange } from '@angular/core';

import { FavoritesForm } from './favorites-form';
import { FavoritesService } from '../../../services/favorites-service';
import { CityService } from '../../../../core/services/city';
import { UserService } from '../../../services/user-service';
import { NotificationService } from '../../../services/notification-service';
import {City} from '../../../../core/models/city.model';

describe('FavoritesForm', () => {
  let component: FavoritesForm;
  let fixture: ComponentFixture<FavoritesForm>;
  let favoritesService: jasmine.SpyObj<FavoritesService>;
  let cityService: jasmine.SpyObj<CityService>;
  let userService: jasmine.SpyObj<UserService>;
  let notificationService: jasmine.SpyObj<NotificationService>;

  const mockCities: City[] = [
    { id: 1, name: 'Montpellier', postalCode:'34000', inseeCode: '34172', areaCode: '243400017', latitude: 43.625, longitude: 3.876, population: 516657, departmentName: 'Herault'},
    { id: 2, name: 'Nîmes', postalCode:'30000', inseeCode: '30189', areaCode: '243000643', latitude: 43.836, longitude: 4.360, population: 150444, departmentName: 'Gard'},

  ];

  beforeEach(async () => {
    const favoritesServiceSpy = jasmine.createSpyObj('FavoritesService', ['addFavorites', 'editFavorites', 'deleteFavorites']);
    const cityServiceSpy = jasmine.createSpyObj('CityService', ['searchCities']);
    const userServiceSpy = jasmine.createSpyObj('UserService', ['fetchUserProfile']);
    const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['showError', 'showSuccess']);

    await TestBed.configureTestingModule({
      imports: [FavoritesForm],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: FavoritesService, useValue: favoritesServiceSpy },
        { provide: CityService, useValue: cityServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoritesForm);
    component = fixture.componentInstance;
    favoritesService = TestBed.inject(FavoritesService) as jasmine.SpyObj<FavoritesService>;
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
      expect(component.favoritesForm).toBeDefined();
      expect(component.favoritesForm.get('activeWeather')?.value).toBeFalse();
      expect(component.favoritesForm.get('activeAirQuality')?.value).toBeFalse();
      expect(component.favoritesForm.get('activePopulation')?.value).toBeFalse();
      expect(component.favoritesForm.get('cityName')?.value).toBe('');
    });

    it('should have required validators on all fields', () => {
      const activeWeatherControl = component.favoritesForm.get('activeWeather');
      const activeAirQualityControl = component.favoritesForm.get('activeAirQuality');
      const activePopulationControl = component.favoritesForm.get('activePopulation');
      const cityNameControl = component.favoritesForm.get('cityName');

      activeWeatherControl?.setValue(null);
      activeAirQualityControl?.setValue(null);
      activePopulationControl?.setValue(null);
      cityNameControl?.setValue('');

      expect(activeWeatherControl?.hasError('required')).toBeTrue();
      expect(activeAirQualityControl?.hasError('required')).toBeTrue();
      expect(activePopulationControl?.hasError('required')).toBeTrue();
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
    it('should patch form with initialFavoriteData when provided', () => {
      const favoriteData = {
        cityId: 1,
        cityName: 'Montpellier',
        selectWeather: true,
        selectAirQuality: false,
        selectPopulation: true
      };

      component.initialFavoriteData = favoriteData;
      component.ngOnChanges({
        initialFavoriteData: new SimpleChange(null, favoriteData, false)
      });

      expect(component.favoritesForm.get('activeWeather')?.value).toBeTrue();
      expect(component.favoritesForm.get('activeAirQuality')?.value).toBeFalse();
      expect(component.favoritesForm.get('activePopulation')?.value).toBeTrue();
      expect(component.favoritesForm.get('cityName')?.value).toBe('Montpellier');
      expect(component.cityIdSelected).toBe(1);
      expect(component.isDeleteMode).toBeFalse();
    });

    it('should handle missing cityName', () => {
      const favoriteData = {
        cityId: 1,
        cityName: '',
        selectWeather: false,
        selectAirQuality: false,
        selectPopulation: false
      };

      component.initialFavoriteData = favoriteData;
      component.ngOnChanges({
        initialFavoriteData: new SimpleChange(null, favoriteData, false)
      });

      expect(component.favoritesForm.get('cityName')?.value).toBe('');
    });

    it('should not patch form when initialFavoriteData is null', () => {
      component.favoritesForm.get('activeWeather')?.setValue(true);
      component.ngOnChanges({});

      expect(component.favoritesForm.get('activeWeather')?.value).toBeTrue();
    });
  });

  describe('City search and selection', () => {
    it('should update cityQuery when onCityInput is called', () => {
      const event = { target: { value: 'Montpellier' } } as any;
      component.onCityInput(event);
      expect(component.cityQuery()).toBe('Montpellier');
    });

    it('should select city and update form', () => {
      component.selectCity(mockCities[0]);

      expect(component.favoritesForm.get('cityName')?.value).toBe('Montpellier');
      expect(component.cityIdSelected).toBe(1);
      expect(component.citySuggestions()).toEqual([]);
    });
  });

  describe('submitForm - Creating new favorite', () => {
    beforeEach(() => {
      component.editingFavoriteId = null;
      component.favoritesForm.patchValue({
        activeWeather: true,
        activeAirQuality: false,
        activePopulation: true,
        cityName: 'Montpellier'
      });
      component.cityIdSelected = 1;
      component.favoritesForm.markAsDirty();
    });

    it('should show error message if form is invalid', () => {
      component.favoritesForm.get('cityName')?.setValue('');
      component.submitForm();

      expect(notificationService.showError).toHaveBeenCalledWith(
        'Veuillez modifier au moins un champ et sélectionner une ville.'
      );
      expect(favoritesService.addFavorites).not.toHaveBeenCalled();
    });

    it('should show error message if cityId is not selected for new entry', () => {
      component.cityIdSelected = null;
      component.submitForm();

      expect(notificationService.showError).toHaveBeenCalledWith(
        'Veuillez modifier au moins un champ et sélectionner une ville.'
      );
    });

    it('should call addFavorites with correct parameters', () => {
      favoritesService.addFavorites.and.returnValue(of(undefined));

      component.submitForm();

      expect(favoritesService.addFavorites).toHaveBeenCalledWith({
        selectWeather: true,
        selectAirQuality: false,
        selectPopulation: true,
        cityId: 1
      });
    });

    it('should set isLoading to true during submission', () => {
      const subject = new Subject<any>();
      favoritesService.addFavorites.and.returnValue(subject.asObservable());

      component.submitForm();

      expect(component.isLoading()).toBeTrue();
      subject.next(undefined);
      subject.complete();
    });

    it('should handle successful favorite creation', fakeAsync(() => {
      favoritesService.addFavorites.and.returnValue(of(undefined));
      spyOn(component.submitSuccess, 'emit');

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(userService.fetchUserProfile).toHaveBeenCalled();
      expect(component.submitSuccess.emit).toHaveBeenCalled();
      expect(component.cityIdSelected).toBeNull();
      expect(component.isDeleteMode).toBeFalse();
    }));

    it('should handle error during favorite creation', fakeAsync(() => {
      favoritesService.addFavorites.and.returnValue(throwError(() => new Error('API Error')));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(notificationService.showError).toHaveBeenCalledWith("Erreur lors de l'enregistrement du favori.");
    }));

    it('should convert falsy values to boolean false in payload', () => {
      favoritesService.addFavorites.and.returnValue(of(undefined));
      component.favoritesForm.patchValue({
        activeWeather: false,
        activeAirQuality: false,
        activePopulation: false
      });

      component.submitForm();

      expect(favoritesService.addFavorites).toHaveBeenCalledWith({
        selectWeather: false,
        selectAirQuality: false,
        selectPopulation: false,
        cityId: 1
      });
    });
  });

  describe('submitForm - Editing existing favorite', () => {
    beforeEach(() => {
      component.editingFavoriteId = 1;
      component.initialFavoriteData = {
        cityId: 1,
        cityName: 'Montpellier',
        selectWeather: true,
        selectAirQuality: true,
        selectPopulation: true
      };
      component.favoritesForm.patchValue({
        activeWeather: true,
        activeAirQuality: false,
        activePopulation: true,
        cityName: 'Montpellier'
      });
      component.cityIdSelected = 1;
      component.favoritesForm.markAsDirty();
    });

    it('should call editFavorites with correct parameters', () => {
      favoritesService.editFavorites.and.returnValue(of(undefined));

      component.submitForm();

      expect(favoritesService.editFavorites).toHaveBeenCalledWith(
        {
          selectWeather: true,
          selectAirQuality: false,
          selectPopulation: true,
          cityId: 1
        },
        1
      );
    });

    it('should handle successful favorite update', fakeAsync(() => {
      favoritesService.editFavorites.and.returnValue(of(undefined));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(userService.fetchUserProfile).toHaveBeenCalled();
    }));

    it('should handle error during favorite update', fakeAsync(() => {
      favoritesService.editFavorites.and.returnValue(throwError(() => new Error('API Error')));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(notificationService.showError).toHaveBeenCalledWith("Erreur lors de l'enregistrement du favori.");
    }));
  });

  describe('submitForm - Deleting favorite', () => {
    beforeEach(() => {
      component.editingFavoriteId = 1;
      component.isDeleteMode = true;
      component.favoritesForm.patchValue({
        activeWeather: true,
        activeAirQuality: false,
        activePopulation: false,
        cityName: 'Paris'
      });
      component.cityIdSelected = 1;
      component.favoritesForm.markAsDirty();
    });

    it('should call deleteFavorites with correct id', () => {
      favoritesService.deleteFavorites.and.returnValue(of(undefined));

      component.submitForm();

      expect(favoritesService.deleteFavorites).toHaveBeenCalledWith(1);
    });

    it('should not call delete if editingFavoriteId is null', () => {
      component.editingFavoriteId = null;
      favoritesService.deleteFavorites.and.returnValue(of(undefined));

      component.submitForm();

      expect(favoritesService.deleteFavorites).not.toHaveBeenCalled();
    });

    it('should handle successful favorite deletion', fakeAsync(() => {
      favoritesService.deleteFavorites.and.returnValue(of(undefined));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(userService.fetchUserProfile).toHaveBeenCalled();
      expect(component.isDeleteMode).toBeFalse();
    }));

    it('should handle error during favorite deletion', fakeAsync(() => {
      favoritesService.deleteFavorites.and.returnValue(throwError(() => new Error('Delete failed')));

      component.submitForm();
      flush();

      expect(component.isLoading()).toBeFalse();
      expect(notificationService.showError).toHaveBeenCalledWith("Erreur lors de la suppression du favori.");
    }));
  });

  describe('onCloseModal', () => {
    it('should reset form to default values', () => {
      component.favoritesForm.patchValue({
        activeWeather: true,
        activeAirQuality: true,
        activePopulation: true,
        cityName: 'Montpellier'
      });

      component.onCloseModal();

      expect(component.favoritesForm.get('activeWeather')?.value).toBeFalse();
      expect(component.favoritesForm.get('activeAirQuality')?.value).toBeFalse();
      expect(component.favoritesForm.get('activePopulation')?.value).toBeFalse();
      expect(component.favoritesForm.get('cityName')?.value).toBe('');
    });

    it('should reset cityIdSelected to null', () => {
      component.cityIdSelected = 1;
      component.onCloseModal();
      expect(component.cityIdSelected).toBeNull();
    });

    it('should reset isDeleteMode to false', () => {
      component.isDeleteMode = true;
      component.onCloseModal();
      expect(component.isDeleteMode).toBeFalse();
    });

    it('should emit closeModal event when closing', () => {
      spyOn(component.closeModal, 'emit');
      component.onCloseModal();
      expect(component.closeModal.emit).toHaveBeenCalled();
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

    it('should initialize cityQuery signal to empty string', () => {
      expect(component.cityQuery()).toBe('');
    });

    it('should initialize citySuggestions signal to empty array', () => {
      expect(component.citySuggestions()).toEqual([]);
    });
  });
});
