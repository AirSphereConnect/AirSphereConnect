import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { WeatherService } from './weather';
import { ApiConfigService } from './api';
import { WeatherMeasurement } from '../models/data.model';

describe('WeatherService', () => {
  let service: WeatherService;
  let httpMock: HttpTestingController;
  let mockApiConfig: jasmine.SpyObj<ApiConfigService>;

  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    mockApiConfig = jasmine.createSpyObj('ApiConfigService', [], { apiUrl: mockApiUrl });

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        WeatherService,
        { provide: ApiConfigService, useValue: mockApiConfig }
      ]
    });

    service = TestBed.inject(WeatherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getHistory', () => {
    it('should fetch weather history for a city', () => {
      const mockApiData = [
        {
          id: 1,
          temperature: 25.5,
          humidity: 65,
          pressure: 1013,
          windSpeed: 15.2,
          windDirection: 180,
          measuredAt: '2024-01-01T12:00:00',
          message: '[{"main":"Clear","description":"clear sky","icon":"01d"}]'
        },
        {
          id: 2,
          temperature: 23.8,
          humidity: 70,
          pressure: 1015,
          windSpeed: 12.5,
          windDirection: 160,
          measuredAt: '2024-01-01T11:00:00',
          message: '[{"main":"Clouds","description":"few clouds","icon":"02d"}]'
        }
      ];

      service.getHistory(123).subscribe(history => {
        expect(history.length).toBe(2);
        expect(history[0].temperature).toBe(25.5);
        expect(history[0].humidity).toBe(65);
        expect(history[0].measuredAt instanceof Date).toBe(true);
        expect(history[1].temperature).toBe(23.8);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/weather/city/history/123`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockApiData);
    });

    it('should return empty array when no history available', () => {
      service.getHistory(456).subscribe(history => {
        expect(history.length).toBe(0);
        expect(Array.isArray(history)).toBe(true);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/weather/city/history/456`);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });

    it('should map API data to WeatherMeasurement model correctly', () => {
      const mockApiData = [{
        id: 10,
        temperature: 20.0,
        humidity: 55,
        pressure: 1010,
        windSpeed: 10.0,
        windDirection: 90,
        measuredAt: '2024-01-15T10:30:00',
        message: '[{"main":"Rain","description":"light rain","icon":"10d"}]'
      }];

      service.getHistory(789).subscribe(history => {
        const weather = history[0];
        expect(weather.id).toBe(10);
        expect(weather.temperature).toBe(20.0);
        expect(weather.humidity).toBe(55);
        expect(weather.pressure).toBe(1010);
        expect(weather.windSpeed).toBe(10.0);
        expect(weather.windDirection).toBe(90);
        expect(weather.message).toBe('[{"main":"Rain","description":"light rain","icon":"10d"}]');
        expect(weather.measuredAt).toEqual(new Date('2024-01-15T10:30:00'));
      });

      const req = httpMock.expectOne(`${mockApiUrl}/weather/city/history/789`);
      req.flush(mockApiData);
    });
  });

  describe('getLatest', () => {
    it('should return the first item from history', () => {
      const mockApiData = [
        {
          id: 1,
          temperature: 26.5,
          humidity: 60,
          pressure: 1012,
          windSpeed: 14.0,
          windDirection: 200,
          measuredAt: '2024-01-01T14:00:00',
          message: '[{"main":"Clear","description":"clear sky","icon":"01d"}]'
        },
        {
          id: 2,
          temperature: 24.0,
          humidity: 65,
          pressure: 1011,
          windSpeed: 13.0,
          windDirection: 190,
          measuredAt: '2024-01-01T13:00:00',
          message: '[{"main":"Clear","description":"clear sky","icon":"01d"}]'
        }
      ];

      service.getLatest(111).subscribe(latest => {
        expect(latest).not.toBeNull();
        expect(latest?.temperature).toBe(26.5);
        expect(latest?.id).toBe(1);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/weather/city/history/111`);
      req.flush(mockApiData);
    });

    it('should return null when history is empty', () => {
      service.getLatest(222).subscribe(latest => {
        expect(latest).toBeNull();
      });

      const req = httpMock.expectOne(`${mockApiUrl}/weather/city/history/222`);
      req.flush([]);
    });
  });
});
