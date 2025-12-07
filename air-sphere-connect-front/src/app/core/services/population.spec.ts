import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PopulationService } from './population';
import { ApiConfigService } from './api';
import { PopulationData } from '../models/data.model';

describe('PopulationService', () => {
  let service: PopulationService;
  let httpMock: HttpTestingController;
  let mockApiConfig: jasmine.SpyObj<ApiConfigService>;

  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    mockApiConfig = jasmine.createSpyObj('ApiConfigService', [], { apiUrl: mockApiUrl });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PopulationService,
        { provide: ApiConfigService, useValue: mockApiConfig }
      ]
    });

    service = TestBed.inject(PopulationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getHistory', () => {
    it('should fetch population history for a city', () => {
      const mockHistory: PopulationData[] = [
        {
          id: 1,
          cityName: 'Paris',
          population: 2165423,
          year: 2023,
          source: 'INSEE'
        },
        {
          id: 2,
          cityName: 'Paris',
          population: 2161063,
          year: 2022,
          source: 'INSEE'
        },
        {
          id: 3,
          cityName: 'Paris',
          population: 2145906,
          year: 2021,
          source: 'INSEE'
        }
      ];

      service.getHistory('Paris').subscribe(history => {
        expect(history.length).toBe(3);
        expect(history[0].cityName).toBe('Paris');
        expect(history[0].population).toBe(2165423);
        expect(history[0].year).toBe(2023);
        expect(history[0].source).toBe('INSEE');
        expect(history[1].year).toBe(2022);
        expect(history[2].year).toBe(2021);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/Paris`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockHistory);
    });

    it('should return empty array when no history available', () => {
      service.getHistory('UnknownCity').subscribe(history => {
        expect(history.length).toBe(0);
        expect(Array.isArray(history)).toBe(true);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/UnknownCity`);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });

    it('should map all PopulationData fields correctly', () => {
      const mockHistory: PopulationData[] = [{
        id: 10,
        cityName: 'Marseille',
        population: 869815,
        year: 2023,
        source: 'INSEE'
      }];

      service.getHistory('Marseille').subscribe(history => {
        const data = history[0];
        expect(data.id).toBe(10);
        expect(data.cityName).toBe('Marseille');
        expect(data.population).toBe(869815);
        expect(data.year).toBe(2023);
        expect(data.source).toBe('INSEE');
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/Marseille`);
      req.flush(mockHistory);
    });

    it('should handle population data without optional fields', () => {
      const mockHistory: PopulationData[] = [{
        population: 516092,
        year: 2023,
        source: 'INSEE'
      }];

      service.getHistory('Lyon').subscribe(history => {
        const data = history[0];
        expect(data.id).toBeUndefined();
        expect(data.cityName).toBeUndefined();
        expect(data.population).toBe(516092);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/Lyon`);
      req.flush(mockHistory);
    });

    it('should handle city names with special characters', () => {
      const cityName = 'Saint-Étienne';
      const mockHistory: PopulationData[] = [{
        id: 1,
        cityName: 'Saint-Étienne',
        population: 172023,
        year: 2023,
        source: 'INSEE'
      }];

      service.getHistory(cityName).subscribe(history => {
        expect(history.length).toBe(1);
        expect(history[0].cityName).toBe('Saint-Étienne');
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/Saint-Étienne`);
      req.flush(mockHistory);
    });
  });

  describe('getTopCities', () => {
    it('should fetch top N cities by population', () => {
      const mockTopCities: PopulationData[] = [
        {
          id: 1,
          cityName: 'Paris',
          population: 2165423,
          year: 2023,
          source: 'INSEE'
        },
        {
          id: 2,
          cityName: 'Marseille',
          population: 869815,
          year: 2023,
          source: 'INSEE'
        },
        {
          id: 3,
          cityName: 'Lyon',
          population: 516092,
          year: 2023,
          source: 'INSEE'
        }
      ];

      service.getTopCities(3).subscribe(cities => {
        expect(cities.length).toBe(3);
        expect(cities[0].cityName).toBe('Paris');
        expect(cities[0].population).toBe(2165423);
        expect(cities[1].cityName).toBe('Marseille');
        expect(cities[2].cityName).toBe('Lyon');
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/top/3`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockTopCities);
    });

    it('should fetch top 10 cities by population', () => {
      const mockTopCities: PopulationData[] = Array.from({ length: 10 }, (_, i) => ({
        id: i + 1,
        cityName: `City${i + 1}`,
        population: 1000000 - i * 50000,
        year: 2023,
        source: 'INSEE'
      }));

      service.getTopCities(10).subscribe(cities => {
        expect(cities.length).toBe(10);
        expect(cities.every(c => c.year === 2023)).toBe(true);
        expect(cities[0].population).toBeGreaterThan(cities[1].population);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/top/10`);
      expect(req.request.method).toBe('GET');
      req.flush(mockTopCities);
    });

    it('should return empty array when no cities available', () => {
      service.getTopCities(5).subscribe(cities => {
        expect(cities.length).toBe(0);
        expect(Array.isArray(cities)).toBe(true);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/top/5`);
      req.flush([]);
    });

    it('should handle different data sources', () => {
      const mockTopCities: PopulationData[] = [
        {
          id: 1,
          cityName: 'Paris',
          population: 2165423,
          year: 2023,
          source: 'INSEE'
        },
        {
          id: 2,
          cityName: 'Lyon',
          population: 516092,
          year: 2022,
          source: 'Recensement'
        }
      ];

      service.getTopCities(2).subscribe(cities => {
        expect(cities[0].source).toBe('INSEE');
        expect(cities[1].source).toBe('Recensement');
        expect(cities[0].year).toBe(2023);
        expect(cities[1].year).toBe(2022);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/history/top/2`);
      req.flush(mockTopCities);
    });
  });
});
