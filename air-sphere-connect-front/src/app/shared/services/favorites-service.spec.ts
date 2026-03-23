import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { FavoritesService } from './favorites-service';
import { ApiConfigService } from '../../core/services/api';
import { AddFavoritePayload, UserProfileResponse } from '../../core/models/user.model';

describe('FavoritesService', () => {
  let service: FavoritesService;
  let httpMock: HttpTestingController;
  let apiConfig: jasmine.SpyObj<ApiConfigService>;
  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    localStorage.clear();

    const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
      apiUrl: mockApiUrl
    });

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        FavoritesService,
        { provide: ApiConfigService, useValue: apiConfigSpy }
      ]
    });

    service = TestBed.inject(FavoritesService);
    httpMock = TestBed.inject(HttpTestingController);
    apiConfig = TestBed.inject(ApiConfigService) as jasmine.SpyObj<ApiConfigService>;
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Constructor', () => {
    it('should load user profile from localStorage on init', () => {
      TestBed.resetTestingModule();
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test' } } as any;
      localStorage.setItem('userProfile', JSON.stringify(mockProfile));

      const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
        apiUrl: mockApiUrl
      });

      TestBed.configureTestingModule({
        providers: [
          provideHttpClient(),
          provideHttpClientTesting(),
          FavoritesService,
          { provide: ApiConfigService, useValue: apiConfigSpy }
        ]
      });

      const newService = TestBed.inject(FavoritesService);
      newService.userProfile$.subscribe(profile => {
        expect(profile).toEqual(mockProfile);
      });
    });

    it('should handle missing localStorage data', () => {
      TestBed.resetTestingModule();
      localStorage.removeItem('userProfile');

      const apiConfigSpy = jasmine.createSpyObj('ApiConfigService', [], {
        apiUrl: mockApiUrl
      });

      TestBed.configureTestingModule({
        providers: [
          provideHttpClient(),
          provideHttpClientTesting(),
          FavoritesService,
          { provide: ApiConfigService, useValue: apiConfigSpy }
        ]
      });

      const newService = TestBed.inject(FavoritesService);
      newService.userProfile$.subscribe(profile => {
        expect(profile).toBeNull();
      });
    });
  });

  describe('fetchUserProfile', () => {
    it('should fetch and set user profile', () => {
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test' } } as any;

      service.fetchUserProfile();

      const req = httpMock.expectOne(`${mockApiUrl}/profile`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(mockProfile);

      service.userProfile$.subscribe(profile => {
        expect(profile).toEqual(mockProfile);
      });
    });

    it('should set null on error', () => {
      service.fetchUserProfile();

      const req = httpMock.expectOne(`${mockApiUrl}/profile`);
      req.error(new ErrorEvent('Network error'));

      service.userProfile$.subscribe(profile => {
        expect(profile).toBeNull();
      });
    });
  });

  describe('getUserId', () => {
    it('should return user id when profile exists', () => {
      const mockProfile: UserProfileResponse = { user: { id: 123, username: 'test' } } as any;
      service.setUserProfile(mockProfile);

      expect(service.getUserId()).toBe(123);
    });

    it('should return null when profile is null', () => {
      service.setUserProfile(null);
      expect(service.getUserId()).toBeNull();
    });
  });

  describe('setUserProfile', () => {
    it('should save profile to localStorage and emit', (done) => {
      const mockProfile: UserProfileResponse = { user: { id: 1, username: 'test' } } as any;

      service.setUserProfile(mockProfile);

      const stored = localStorage.getItem('userProfile');
      expect(stored).toBe(JSON.stringify(mockProfile));

      service.userProfile$.subscribe(profile => {
        expect(profile).toEqual(mockProfile);
        done();
      });
    });

    it('should remove profile from localStorage when null', () => {
      localStorage.setItem('userProfile', 'some data');
      service.setUserProfile(null);

      expect(localStorage.getItem('userProfile')).toBeNull();
    });
  });

  describe('addFavorites', () => {
    it('should add a new favorite', () => {
      const payload: AddFavoritePayload = {
        cityId: 1,
        selectWeather: true,
        selectAirQuality: false,
        selectPopulation: true
      };

      service.addFavorites(payload).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/favorites/new`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(payload);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });

  describe('editFavorites', () => {
    it('should edit an existing favorite', () => {
      const payload: AddFavoritePayload = {
        cityId: 2,
        selectWeather: false,
        selectAirQuality: true,
        selectPopulation: false
      };
      const favoriteId = 5;

      service.editFavorites(payload, favoriteId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/favorites/${favoriteId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(payload);
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });

  describe('deleteFavorites', () => {
    it('should delete a favorite', () => {
      const favoriteId = 3;

      service.deleteFavorites(favoriteId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/favorites/${favoriteId}`);
      expect(req.request.method).toBe('DELETE');
      expect(req.request.withCredentials).toBeTrue();
      req.flush(null);
    });
  });
});
