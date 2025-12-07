import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AirQualityService } from './air-quality';
import { ApiConfigService } from './api';
import { AirQualityComplete, AirQualityData } from '../models/data.model';

describe('AirQualityService', () => {
  let service: AirQualityService;
  let httpMock: HttpTestingController;
  let mockApiConfig: jasmine.SpyObj<ApiConfigService>;

  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    mockApiConfig = jasmine.createSpyObj('ApiConfigService', [], { apiUrl: mockApiUrl });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AirQualityService,
        { provide: ApiConfigService, useValue: mockApiConfig }
      ]
    });

    service = TestBed.inject(AirQualityService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getComplete', () => {
    it('should fetch complete air quality data for a city', () => {
      const mockResponse: AirQualityComplete = {
        latestMeasurement: {
          pm25: 15.5,
          pm10: 25.3,
          no2: 30.2,
          o3: 45.1,
          so2: 5.5,
          co: 0.5,
          unit: 'µg/m³',
          measuredAt: '2024-01-01T12:00:00'
        },
        latestIndex: {
          qualityIndex: 3,
          qualityLabel: 'Moyen',
          qualityColor: 'orange',
          alert: false,
          areaName: 'Paris',
          measuredAt: '2024-01-01T12:00:00'
        },
        measurementHistory: [],
        indexHistory: []
      };

      service.getComplete('Paris').subscribe(data => {
        expect(data).toEqual(mockResponse);
        expect(data.latestMeasurement).toBeTruthy();
        expect(data.latestIndex).toBeTruthy();
        expect(Array.isArray(data.measurementHistory)).toBe(true);
        expect(Array.isArray(data.indexHistory)).toBe(true);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/air-quality/city/Paris/complete`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockResponse);
    });
  });

  describe('getLatestIndex', () => {
    it('should fetch only the latest air quality index', () => {
      const mockComplete: AirQualityComplete = {
        latestMeasurement: {
          pm25: 10.0,
          pm10: 20.0,
          no2: 25.0,
          o3: 40.0,
          so2: 4.0,
          co: 0.4,
          unit: 'µg/m³',
          measuredAt: '2024-01-01T12:00:00'
        },
        latestIndex: {
          qualityIndex: 2,
          qualityLabel: 'Bon',
          qualityColor: 'green',
          alert: false,
          areaName: 'Lyon',
          measuredAt: '2024-01-01T12:00:00'
        },
        measurementHistory: [],
        indexHistory: []
      };

      service.getLatestIndex('Lyon').subscribe(index => {
        expect(index).toEqual(mockComplete.latestIndex);
        expect(index?.qualityLabel).toBe('Bon');
      });

      const req = httpMock.expectOne(`${mockApiUrl}/air-quality/city/Lyon/complete`);
      expect(req.request.method).toBe('GET');
      req.flush(mockComplete);
    });
  });

  describe('getLatestMeasurement', () => {
    it('should fetch only the latest air quality measurement', () => {
      const mockComplete: AirQualityComplete = {
        latestMeasurement: {
          pm25: 12.5,
          pm10: 20.3,
          no2: 25.2,
          o3: 40.1,
          so2: 4.5,
          co: 0.6,
          unit: 'µg/m³',
          measuredAt: '2024-01-01T12:00:00'
        },
        latestIndex: {
          qualityIndex: 1,
          qualityLabel: 'Bon',
          qualityColor: 'green',
          alert: false,
          areaName: 'Marseille',
          measuredAt: '2024-01-01T12:00:00'
        },
        measurementHistory: [],
        indexHistory: []
      };

      service.getLatestMeasurement('Marseille').subscribe(measurement => {
        expect(measurement).toEqual(mockComplete.latestMeasurement);
        expect(measurement?.pm25).toBe(12.5);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/air-quality/city/Marseille/complete`);
      expect(req.request.method).toBe('GET');
      req.flush(mockComplete);
    });
  });

  describe('getTopCitiesInDepartment', () => {
    it('should fetch top cities in department with default limit', () => {
      const mockCities: AirQualityData[] = [
        { cityId: 1, cityName: 'Montpellier', pm25: 15.5, pm10: 25.3, no2: 30.2, o3: 45.0, so2: 5.0 },
        { cityId: 2, cityName: 'Béziers', pm25: 14.2, pm10: 22.1, no2: 28.5, o3: 42.0, so2: 4.5 }
      ];

      service.getTopCitiesInDepartment('34').subscribe(cities => {
        expect(cities.length).toBe(2);
        expect(cities).toEqual(mockCities);
        expect(cities[0].cityName).toBe('Montpellier');
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/air-quality/department/34/top-cities` &&
        request.params.get('limit') === '2'
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockCities);
    });

    it('should fetch top cities with custom limit', () => {
      const mockCities: AirQualityData[] = [
        { cityId: 1, cityName: 'City1', pm25: 15.5, pm10: 25.3, no2: 30.2, o3: 45.0, so2: 5.0 },
        { cityId: 2, cityName: 'City2', pm25: 14.2, pm10: 22.1, no2: 28.5, o3: 42.0, so2: 4.5 },
        { cityId: 3, cityName: 'City3', pm25: 13.8, pm10: 21.5, no2: 27.1, o3: 40.0, so2: 4.0 }
      ];

      service.getTopCitiesInDepartment('75', 3).subscribe(cities => {
        expect(cities.length).toBe(3);
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/air-quality/department/75/top-cities` &&
        request.params.get('limit') === '3'
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockCities);
    });
  });
});
