import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { CityPopulationChart } from './city-population-chart';
import { CityService } from '../../../../core/services/city';
import { City } from '../../../../core/models/city.model';
import { GroupedBar } from '@unovis/ts';

describe('CityPopulationChart', () => {
  let component: CityPopulationChart;
  let fixture: ComponentFixture<CityPopulationChart>;
  let mockCityService: jasmine.SpyObj<CityService>;

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

  const mockTopCities: City[] = [
    mockCity,
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

  beforeEach(async () => {
    mockCityService = jasmine.createSpyObj('CityService', ['getTopCitiesByArea']);

    await TestBed.configureTestingModule({
      imports: [CityPopulationChart],
      providers: [{ provide: CityService, useValue: mockCityService }]
    }).compileComponents();

    fixture = TestBed.createComponent(CityPopulationChart);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    mockCityService.getTopCitiesByArea.and.returnValue(of([]));
    fixture.componentRef.setInput('selectedCity', mockCity);
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  describe('data loading', () => {
    it('should load top cities when selectedCity input changes', () => {
      mockCityService.getTopCitiesByArea.and.returnValue(of(mockTopCities));

      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();

      expect(mockCityService.getTopCitiesByArea).toHaveBeenCalledWith('75', 10);
      expect(component.chartData()).toEqual(mockTopCities);
    });

    it('should call service with correct areaCode', () => {
      const lyonCity: City = { ...mockCity, id: 2, name: 'Lyon', areaCode: '69' };
      mockCityService.getTopCitiesByArea.and.returnValue(of([]));

      fixture.componentRef.setInput('selectedCity', lyonCity);
      fixture.detectChanges();

      expect(mockCityService.getTopCitiesByArea).toHaveBeenCalledWith('69', 10);
    });

    it('should request top 10 cities', () => {
      mockCityService.getTopCitiesByArea.and.returnValue(of(mockTopCities));

      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();

      expect(mockCityService.getTopCitiesByArea).toHaveBeenCalledWith(jasmine.any(String), 10);
    });

    it('should handle empty response', () => {
      mockCityService.getTopCitiesByArea.and.returnValue(of([]));

      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();

      expect(component.chartData()).toEqual([]);
    });

    it('should handle error when loading cities', () => {
      mockCityService.getTopCitiesByArea.and.returnValue(
        throwError(() => new Error('Network error'))
      );

      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();

      // Should not throw, error is handled
      expect(component.chartData()).toEqual([]);
    });
  });

  describe('xTickValues computation', () => {
    it('should return indices for all cities', () => {
      mockCityService.getTopCitiesByArea.and.returnValue(of(mockTopCities));

      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();

      const ticks = component.xTickValues();

      expect(ticks).toEqual([0, 1, 2]);
    });

    it('should return empty array when no data', () => {
      mockCityService.getTopCitiesByArea.and.returnValue(of([]));

      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();

      const ticks = component.xTickValues();

      expect(ticks).toEqual([]);
    });
  });

  describe('accessor functions', () => {
    beforeEach(() => {
      mockCityService.getTopCitiesByArea.and.returnValue(of(mockTopCities));
      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();
    });

    it('should extract x value as index', () => {
      const result = component.x(mockCity, 5);

      expect(result).toBe(5);
    });

    it('should extract y value as population', () => {
      const result = component.y(mockCity);

      expect(result).toBe(2165423);
    });

    it('should return 0 for missing population', () => {
      const cityWithoutPop: City = { ...mockCity, population: 0 };
      const result = component.y(cityWithoutPop);

      expect(result).toBe(0);
    });
  });

  describe('formatting functions', () => {
    beforeEach(() => {
      mockCityService.getTopCitiesByArea.and.returnValue(of(mockTopCities));
      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();
    });

    it('should format city names, truncating long names', () => {
      component.chartData.set([
        { ...mockCity, name: 'Short' },
        { ...mockCity, name: 'VeryLongCityName' }
      ]);

      expect(component.xTickFormat(0, 0)).toBe('Short');
      expect(component.xTickFormat(1, 1)).toBe('VeryL.');
    });

    it('should format large populations as millions', () => {
      const formatted = component.yTickFormat(2_500_000);

      expect(formatted).toBe('2.5M');
    });

    it('should format medium populations as thousands', () => {
      const formatted = component.yTickFormat(15_000);

      expect(formatted).toBe('15k');
    });

    it('should format small populations as is', () => {
      const formatted = component.yTickFormat(500);

      expect(formatted).toBe('500');
    });

    it('should handle Date in yTickFormat by returning 0', () => {
      const formatted = component.yTickFormat(new Date());

      expect(formatted).toBe('0');
    });
  });

  describe('tooltip generation', () => {
    beforeEach(() => {
      mockCityService.getTopCitiesByArea.and.returnValue(of(mockTopCities));
      fixture.componentRef.setInput('selectedCity', mockCity);
      fixture.detectChanges();
    });

    it('should generate tooltip HTML with city name and population', () => {
      const html = component.tooltipTriggers[GroupedBar.selectors.bar](mockCity);

      expect(html).toContain('Paris');
      expect(html).toContain('2');
      expect(html).toContain('165');
      expect(html).toContain('423');
      expect(html).toContain('habitants');
    });

    it('should format population with French locale', () => {
      const html = component.tooltipTriggers[GroupedBar.selectors.bar](mockCity);

      // French locale uses spaces as thousand separators
      expect(html).toContain('165'); // Part of "2 165 423"
    });

    it('should handle city with zero population', () => {
      const cityWithZeroPop: City = { ...mockCity, population: 0 };
      const html = component.tooltipTriggers[GroupedBar.selectors.bar](cityWithZeroPop);

      expect(html).toContain('0');
      expect(html).toContain('habitants');
    });
  });
});
