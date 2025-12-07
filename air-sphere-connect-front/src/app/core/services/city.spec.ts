import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CityService } from './city';
import { ApiConfigService } from './api';
import { City } from '../models/city.model';

describe('CityService', () => {
  let service: CityService;
  let httpMock: HttpTestingController;
  let mockApiConfig: jasmine.SpyObj<ApiConfigService>;

  const mockApiUrl = 'http://localhost:8080/api';

  beforeEach(() => {
    mockApiConfig = jasmine.createSpyObj('ApiConfigService', [], { apiUrl: mockApiUrl });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        CityService,
        { provide: ApiConfigService, useValue: mockApiConfig }
      ]
    });

    service = TestBed.inject(CityService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('searchCities', () => {
    it('should search cities by partial query', () => {
      const mockCities: City[] = [
        {
          id: 1,
          name: 'Toulouse',
          postalCode: '31000',
          inseeCode: '31555',
          areaCode: '31',
          latitude: 43.6047,
          longitude: 1.4442,
          population: 479553,
          departmentName: 'Haute-Garonne'
        },
        {
          id: 2,
          name: 'Toulon',
          postalCode: '83000',
          inseeCode: '83137',
          areaCode: '83',
          latitude: 43.1242,
          longitude: 5.928,
          population: 171953,
          departmentName: 'Var'
        }
      ];

      service.searchCities('Tou').subscribe(cities => {
        expect(cities.length).toBe(2);
        expect(cities[0].name).toBe('Toulouse');
        expect(cities[0].postalCode).toBe('31000');
        expect(cities[0].population).toBe(479553);
        expect(cities[1].name).toBe('Toulon');
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/cities/search-name` &&
        request.params.get('query') === 'Tou'
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBeFalsy();
      req.flush(mockCities);
    });

    it('should return empty array when no cities match query', () => {
      service.searchCities('XYZ123').subscribe(cities => {
        expect(cities.length).toBe(0);
        expect(Array.isArray(cities)).toBe(true);
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/cities/search-name` &&
        request.params.get('query') === 'XYZ123'
      );
      req.flush([]);
    });

    it('should map all City model fields correctly', () => {
      const mockCities: City[] = [{
        id: 10,
        name: 'Paris',
        postalCode: '75001',
        inseeCode: '75056',
        areaCode: '75',
        latitude: 48.8566,
        longitude: 2.3522,
        population: 2165423,
        departmentName: 'Paris'
      }];

      service.searchCities('Paris').subscribe(cities => {
        const city = cities[0];
        expect(city.id).toBe(10);
        expect(city.name).toBe('Paris');
        expect(city.postalCode).toBe('75001');
        expect(city.inseeCode).toBe('75056');
        expect(city.areaCode).toBe('75');
        expect(city.latitude).toBe(48.8566);
        expect(city.longitude).toBe(2.3522);
        expect(city.population).toBe(2165423);
        expect(city.departmentName).toBe('Paris');
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/cities/search-name`
      );
      req.flush(mockCities);
    });
  });

  describe('getByName', () => {
    it('should fetch city by exact name', () => {
      const mockCity: City = {
        id: 5,
        name: 'Montpellier',
        postalCode: '34000',
        inseeCode: '34172',
        areaCode: '34',
        latitude: 43.6108,
        longitude: 3.8767,
        population: 290053,
        departmentName: 'Hérault'
      };

      service.getByName('Montpellier').subscribe(city => {
        expect(city).toEqual(mockCity);
        expect(city.name).toBe('Montpellier');
        expect(city.areaCode).toBe('34');
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/cities/city` &&
        request.params.get('name') === 'Montpellier'
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockCity);
    });

    it('should handle city without departmentName', () => {
      const mockCity: City = {
        id: 7,
        name: 'Lyon',
        postalCode: '69001',
        inseeCode: '69123',
        areaCode: '69',
        latitude: 45.764,
        longitude: 4.8357,
        population: 516092
      };

      service.getByName('Lyon').subscribe(city => {
        expect(city.departmentName).toBeUndefined();
        expect(city.name).toBe('Lyon');
      });

      const req = httpMock.expectOne(request =>
        request.url === `${mockApiUrl}/cities/city` &&
        request.params.get('name') === 'Lyon'
      );
      req.flush(mockCity);
    });
  });

  describe('getAll', () => {
    it('should fetch all cities', () => {
      const mockCities: City[] = [
        {
          id: 1,
          name: 'Paris',
          postalCode: '75001',
          inseeCode: '75056',
          areaCode: '75',
          latitude: 48.8566,
          longitude: 2.3522,
          population: 2165423,
          departmentName: 'Paris'
        },
        {
          id: 2,
          name: 'Marseille',
          postalCode: '13001',
          inseeCode: '13055',
          areaCode: '13',
          latitude: 43.2965,
          longitude: 5.3698,
          population: 869815,
          departmentName: 'Bouches-du-Rhône'
        },
        {
          id: 3,
          name: 'Lyon',
          postalCode: '69001',
          inseeCode: '69123',
          areaCode: '69',
          latitude: 45.764,
          longitude: 4.8357,
          population: 516092,
          departmentName: 'Rhône'
        }
      ];

      service.getAll().subscribe(cities => {
        expect(cities.length).toBe(3);
        expect(cities[0].name).toBe('Paris');
        expect(cities[1].name).toBe('Marseille');
        expect(cities[2].name).toBe('Lyon');
        expect(Array.isArray(cities)).toBe(true);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/cities`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockCities);
    });

    it('should return empty array when no cities available', () => {
      service.getAll().subscribe(cities => {
        expect(cities.length).toBe(0);
        expect(Array.isArray(cities)).toBe(true);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/cities`);
      req.flush([]);
    });
  });

  describe('getTopCitiesByArea', () => {
    it('should fetch top cities by area code with specified limit', () => {
      const mockCities: City[] = [
        {
          id: 1,
          name: 'Toulouse',
          postalCode: '31000',
          inseeCode: '31555',
          areaCode: '31',
          latitude: 43.6047,
          longitude: 1.4442,
          population: 479553,
          departmentName: 'Haute-Garonne'
        },
        {
          id: 2,
          name: 'Colomiers',
          postalCode: '31770',
          inseeCode: '31149',
          areaCode: '31',
          latitude: 43.6106,
          longitude: 1.3364,
          population: 38918,
          departmentName: 'Haute-Garonne'
        }
      ];

      service.getTopCitiesByArea('31', 2).subscribe(cities => {
        expect(cities.length).toBe(2);
        expect(cities[0].name).toBe('Toulouse');
        expect(cities[0].areaCode).toBe('31');
        expect(cities[1].name).toBe('Colomiers');
        expect(cities[1].areaCode).toBe('31');
      });

      const req = httpMock.expectOne(`${mockApiUrl}/cities/area/31/top/2`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockCities);
    });

    it('should fetch top 10 cities by area code', () => {
      const mockCities: City[] = Array.from({ length: 10 }, (_, i) => ({
        id: i + 1,
        name: `City${i + 1}`,
        postalCode: `75${String(i).padStart(3, '0')}`,
        inseeCode: `75${String(i).padStart(3, '0')}`,
        areaCode: '75',
        latitude: 48.8566 + i * 0.01,
        longitude: 2.3522 + i * 0.01,
        population: 100000 - i * 1000,
        departmentName: 'Paris'
      }));

      service.getTopCitiesByArea('75', 10).subscribe(cities => {
        expect(cities.length).toBe(10);
        expect(cities.every(c => c.areaCode === '75')).toBe(true);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/cities/area/75/top/10`);
      expect(req.request.method).toBe('GET');
      req.flush(mockCities);
    });

    it('should return empty array when no cities in area', () => {
      service.getTopCitiesByArea('99', 5).subscribe(cities => {
        expect(cities.length).toBe(0);
        expect(Array.isArray(cities)).toBe(true);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/cities/area/99/top/5`);
      req.flush([]);
    });
  });
});
