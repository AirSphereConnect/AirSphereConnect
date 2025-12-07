import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WeatherHistory } from './weather-history';
import { CityHistoryData, CityDailySnapshot, City } from '../../../../core/models/city.model';

describe('WeatherHistory', () => {
  let component: WeatherHistory;
  let fixture: ComponentFixture<WeatherHistory>;

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

  const createSnapshot = (date: string, hasWeather: boolean): CityDailySnapshot => ({
    date: new Date(date),
    weather: hasWeather ? {
      id: 1,
      temperature: 20.5,
      humidity: 65,
      pressure: 1013,
      windSpeed: 15.2,
      windDirection: 180,
      measuredAt: new Date(date),
      message: '[{"main":"Clear","description":"clear sky","icon":"01d"}]'
    } : null,
    airMeasurement: null,
    airIndex: null
  });

  const mockHistoryData: CityHistoryData = {
    city: mockCity,
    dailySnapshots: [
      createSnapshot('2024-01-15T12:00:00', true),
      createSnapshot('2024-01-14T12:00:00', true),
      createSnapshot('2024-01-13T12:00:00', true),
      createSnapshot('2024-01-12T12:00:00', true),
      createSnapshot('2024-01-11T12:00:00', true)
    ]
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WeatherHistory]
    }).compileComponents();

    fixture = TestBed.createComponent(WeatherHistory);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.componentRef.setInput('historyData', mockHistoryData);
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  describe('data filtering', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
    });

    it('should return all data when no dates provided', () => {
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered.length).toBe(5);
    });

    it('should filter data by start date', () => {
      fixture.componentRef.setInput('startDate', '2024-01-13');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered.length).toBe(3); // Jan 13, 14, 15
    });

    it('should filter data by end date', () => {
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '2024-01-13');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered.length).toBe(3); // Jan 11, 12, 13
    });

    it('should filter data by date range', () => {
      fixture.componentRef.setInput('startDate', '2024-01-12');
      fixture.componentRef.setInput('endDate', '2024-01-14');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered.length).toBe(3); // Jan 12, 13, 14
    });
  });

  describe('pagination', () => {
    beforeEach(() => {
      const largeDataset: CityHistoryData = {
        city: mockCity,
        dailySnapshots: Array.from({ length: 25 }, (_, i) =>
          createSnapshot(`2024-01-${String(i + 1).padStart(2, '0')}T12:00:00`, true)
        )
      };
      fixture.componentRef.setInput('historyData', largeDataset);
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();
    });

    it('should start on page 1', () => {
      expect(component.currentPage()).toBe(1);
    });

    it('should show 10 items per page', () => {
      const paginated = component.filteredData();

      expect(paginated.length).toBe(10);
    });

    it('should calculate total pages correctly', () => {
      const total = component.totalPages();

      expect(total).toBe(3); // 25 / 10 = 3 pages
    });

    it('should navigate to next page', () => {
      component.nextPage();

      expect(component.currentPage()).toBe(2);
    });

    it('should navigate to previous page', () => {
      component.currentPage.set(2);
      component.prevPage();

      expect(component.currentPage()).toBe(1);
    });

    it('should not go below page 1', () => {
      component.prevPage();

      expect(component.currentPage()).toBe(1);
    });

    it('should not exceed total pages', () => {
      component.currentPage.set(3);
      component.nextPage();

      expect(component.currentPage()).toBe(3);
    });

    it('should go to specific page', () => {
      component.goToPage(2);

      expect(component.currentPage()).toBe(2);
    });

    it('should not go to invalid page', () => {
      component.goToPage(10);

      expect(component.currentPage()).toBe(1);
    });
  });

  describe('weather data detection', () => {
    it('should detect when weather data exists', () => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();

      expect(component.hasWeatherData()).toBe(true);
    });

    it('should detect when only air quality data exists', () => {
      const airQualityOnlyData: CityHistoryData = {
        city: mockCity,
        dailySnapshots: [createSnapshot('2024-01-15T12:00:00', false)]
      };

      fixture.componentRef.setInput('historyData', airQualityOnlyData);
      fixture.detectChanges();

      expect(component.hasWeatherData()).toBe(false);
      expect(component.hasOnlyAirQualityData()).toBe(true);
    });

    it('should return false for empty data', () => {
      const emptyData: CityHistoryData = {
        city: mockCity,
        dailySnapshots: []
      };

      fixture.componentRef.setInput('historyData', emptyData);
      fixture.detectChanges();

      expect(component.hasWeatherData()).toBe(false);
      expect(component.hasOnlyAirQualityData()).toBe(false);
    });
  });

  describe('wind direction formatting', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.detectChanges();
    });

    it('should return N for 0 degrees', () => {
      expect(component.getWindDirection(0)).toBe('N');
    });

    it('should return NE for 45 degrees', () => {
      expect(component.getWindDirection(45)).toBe('NE');
    });

    it('should return E for 90 degrees', () => {
      expect(component.getWindDirection(90)).toBe('E');
    });

    it('should return SE for 135 degrees', () => {
      expect(component.getWindDirection(135)).toBe('SE');
    });

    it('should return S for 180 degrees', () => {
      expect(component.getWindDirection(180)).toBe('S');
    });

    it('should return SO for 225 degrees', () => {
      expect(component.getWindDirection(225)).toBe('SO');
    });

    it('should return O for 270 degrees', () => {
      expect(component.getWindDirection(270)).toBe('O');
    });

    it('should return NO for 315 degrees', () => {
      expect(component.getWindDirection(315)).toBe('NO');
    });

    it('should return "Non mesuré" for undefined', () => {
      expect(component.getWindDirection(undefined)).toBe('Non mesuré');
    });

    it('should handle 360 degrees as N', () => {
      expect(component.getWindDirection(360)).toBe('N');
    });
  });

  describe('temperature formatting', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.detectChanges();
    });

    it('should format temperature with 1 decimal', () => {
      expect(component.formatTemperature(20.567)).toBe('20.6°C');
    });

    it('should format integer temperature', () => {
      expect(component.formatTemperature(20)).toBe('20.0°C');
    });

    it('should handle negative temperatures', () => {
      expect(component.formatTemperature(-5.3)).toBe('-5.3°C');
    });

    it('should return "Non mesuré" for undefined', () => {
      expect(component.formatTemperature(undefined)).toBe('Non mesuré');
    });
  });

  describe('humidity formatting', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.detectChanges();
    });

    it('should format humidity as percentage', () => {
      expect(component.formatHumidity(65)).toBe('65%');
    });

    it('should handle 0% humidity', () => {
      expect(component.formatHumidity(0)).toBe('0%');
    });

    it('should handle 100% humidity', () => {
      expect(component.formatHumidity(100)).toBe('100%');
    });

    it('should return "Non mesuré" for undefined', () => {
      expect(component.formatHumidity(undefined)).toBe('Non mesuré');
    });
  });

  describe('pressure formatting', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.detectChanges();
    });

    it('should format pressure in hPa', () => {
      expect(component.formatPressure(1013)).toBe('1013 hPa');
    });

    it('should handle decimal pressure', () => {
      expect(component.formatPressure(1013.25)).toBe('1013.25 hPa');
    });

    it('should return "Non mesuré" for undefined', () => {
      expect(component.formatPressure(undefined)).toBe('Non mesuré');
    });
  });

  describe('wind speed formatting', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.detectChanges();
    });

    it('should format wind speed with 1 decimal', () => {
      expect(component.formatWindSpeed(15.267)).toBe('15.3 m/s');
    });

    it('should format integer wind speed', () => {
      expect(component.formatWindSpeed(10)).toBe('10.0 m/s');
    });

    it('should handle 0 wind speed', () => {
      expect(component.formatWindSpeed(0)).toBe('0.0 m/s');
    });

    it('should return "Non mesuré" for undefined', () => {
      expect(component.formatWindSpeed(undefined)).toBe('Non mesuré');
    });
  });

  describe('weather icon extraction', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.detectChanges();
    });

    it('should extract icon from JSON string', () => {
      const message = '[{"main":"Clear","description":"clear sky","icon":"01d"}]';
      const icon = component.getWeatherIcon(message);

      expect(icon).toBe('01d');
    });

    it('should extract icon from JSON array', () => {
      const message = '[{"icon":"02n"}]';
      const icon = component.getWeatherIcon(message);

      expect(icon).toBe('02n');
    });

    it('should handle object instead of array', () => {
      const message = '{"icon":"03d"}';
      const icon = component.getWeatherIcon(message);

      expect(icon).toBe('03d');
    });

    it('should return null for invalid JSON', () => {
      const icon = component.getWeatherIcon('invalid json');

      expect(icon).toBeNull();
    });

    it('should return null for undefined message', () => {
      const icon = component.getWeatherIcon(undefined);

      expect(icon).toBeNull();
    });

    it('should return null for empty array', () => {
      const icon = component.getWeatherIcon('[]');

      expect(icon).toBeNull();
    });

    it('should return null when icon field missing', () => {
      const message = '[{"main":"Clear"}]';
      const icon = component.getWeatherIcon(message);

      expect(icon).toBeNull();
    });
  });

  describe('weather icon URL generation', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.detectChanges();
    });

    it('should generate correct OpenWeatherMap URL', () => {
      const url = component.getWeatherIconUrl('01d');

      expect(url).toBe('https://openweathermap.org/img/wn/01d@2x.png');
    });

    it('should handle different icon codes', () => {
      expect(component.getWeatherIconUrl('10n')).toContain('10n@2x.png');
      expect(component.getWeatherIconUrl('50d')).toContain('50d@2x.png');
    });
  });

  describe('edge cases', () => {
    it('should handle single day data', () => {
      const singleDayData: CityHistoryData = {
        city: mockCity,
        dailySnapshots: [createSnapshot('2024-01-15T12:00:00', true)]
      };

      fixture.componentRef.setInput('historyData', singleDayData);
      fixture.detectChanges();

      expect(component.filteredData().length).toBe(1);
      expect(component.totalPages()).toBe(1);
    });

    it('should handle data with mixed weather availability', () => {
      const mixedData: CityHistoryData = {
        city: mockCity,
        dailySnapshots: [
          createSnapshot('2024-01-15T12:00:00', true),
          createSnapshot('2024-01-14T12:00:00', false),
          createSnapshot('2024-01-13T12:00:00', true)
        ]
      };

      fixture.componentRef.setInput('historyData', mixedData);
      fixture.detectChanges();

      expect(component.hasWeatherData()).toBe(true);
    });
  });
});
