import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TemperatureChart } from './temperature-chart';
import { WeatherMeasurement } from '../../../../core/models/data.model';

describe('TemperatureChart', () => {
  let component: TemperatureChart;
  let fixture: ComponentFixture<TemperatureChart>;

  const mockWeatherData: WeatherMeasurement[] = [
    {
      id: 1,
      temperature: 25.5,
      humidity: 65,
      pressure: 1013,
      windSpeed: 15.2,
      windDirection: 180,
      measuredAt: new Date('2024-01-01T12:00:00'),
      message: 'Clear'
    },
    {
      id: 2,
      temperature: 23.8,
      humidity: 70,
      pressure: 1015,
      windSpeed: 12.5,
      windDirection: 160,
      measuredAt: new Date('2024-01-02T12:00:00'),
      message: 'Clouds'
    },
    {
      id: 3,
      temperature: 26.2,
      humidity: 60,
      pressure: 1012,
      windSpeed: 14.0,
      windDirection: 170,
      measuredAt: new Date('2024-01-03T12:00:00'),
      message: 'Clear'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TemperatureChart]
    }).compileComponents();

    fixture = TestBed.createComponent(TemperatureChart);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.componentRef.setInput('data', []);
    fixture.componentRef.setInput('cityName', 'Test City');
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  describe('chartData computation', () => {
    it('should compute chart data from weather measurements', () => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData.length).toBe(3);
      expect(chartData[0].y).toBe(25.5);
      expect(chartData[1].y).toBe(23.8);
      expect(chartData[2].y).toBe(26.2);
    });

    it('should group multiple measurements on same day and calculate average', () => {
      const sameDayData: WeatherMeasurement[] = [
        { ...mockWeatherData[0], temperature: 20.0, measuredAt: new Date('2024-01-01T08:00:00') },
        { ...mockWeatherData[0], temperature: 25.0, measuredAt: new Date('2024-01-01T12:00:00') },
        { ...mockWeatherData[0], temperature: 30.0, measuredAt: new Date('2024-01-01T18:00:00') }
      ];

      fixture.componentRef.setInput('data', sameDayData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData.length).toBe(1);
      expect(chartData[0].y).toBe(25.0); // (20 + 25 + 30) / 3
    });

    it('should sort chart data by date in ascending order', () => {
      const unsortedData: WeatherMeasurement[] = [
        { ...mockWeatherData[0], measuredAt: new Date('2024-01-03T12:00:00') },
        { ...mockWeatherData[0], measuredAt: new Date('2024-01-01T12:00:00') },
        { ...mockWeatherData[0], measuredAt: new Date('2024-01-02T12:00:00') }
      ];

      fixture.componentRef.setInput('data', unsortedData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData[0].x.getDate()).toBe(1);
      expect(chartData[1].x.getDate()).toBe(2);
      expect(chartData[2].x.getDate()).toBe(3);
    });

    it('should return empty array when no data provided', () => {
      fixture.componentRef.setInput('data', []);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData.length).toBe(0);
      expect(Array.isArray(chartData)).toBe(true);
    });

    it('should handle single measurement', () => {
      fixture.componentRef.setInput('data', [mockWeatherData[0]]);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData.length).toBe(1);
      expect(chartData[0].y).toBe(25.5);
    });

    it('should create Date objects for x values', () => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData[0].x).toBeInstanceOf(Date);
      expect(chartData[1].x).toBeInstanceOf(Date);
    });
  });

  describe('yDomain computation', () => {
    it('should calculate y domain with margins for normal data', () => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const yDomain = component.yDomain();

      // Min temp is 23.8, max is 26.2
      // Range is 2.4, margin should be ~0.24 (but minimum 2)
      expect(yDomain[0]).toBeLessThan(23.8);
      expect(yDomain[1]).toBeGreaterThan(26.2);
    });

    it('should return default domain [0, 30] for empty data', () => {
      fixture.componentRef.setInput('data', []);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const yDomain = component.yDomain();

      expect(yDomain).toEqual([0, 30]);
    });

    it('should handle single data point with 2°C minimum margin', () => {
      const singleData: WeatherMeasurement[] = [
        { ...mockWeatherData[0], temperature: 20.0 }
      ];

      fixture.componentRef.setInput('data', singleData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const yDomain = component.yDomain();

      expect(yDomain[0]).toBeLessThanOrEqual(18); // 20 - 2
      expect(yDomain[1]).toBeGreaterThanOrEqual(22); // 20 + 2
    });

    it('should apply 10% margin for large temperature ranges', () => {
      const wideRangeData: WeatherMeasurement[] = [
        { ...mockWeatherData[0], temperature: 10.0, measuredAt: new Date('2024-01-01T12:00:00') },
        { ...mockWeatherData[0], temperature: 30.0, measuredAt: new Date('2024-01-02T12:00:00') }
      ];

      fixture.componentRef.setInput('data', wideRangeData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const yDomain = component.yDomain();

      // Range is 20, margin should be 2 (10%)
      expect(yDomain[0]).toBeLessThanOrEqual(8);  // 10 - 2
      expect(yDomain[1]).toBeGreaterThanOrEqual(32); // 30 + 2
    });
  });

  describe('xTickValues computation', () => {
    it('should return empty array for no data', () => {
      fixture.componentRef.setInput('data', []);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const ticks = component.xTickValues();

      expect(ticks.length).toBe(0);
    });

    it('should return single tick for single data point', () => {
      fixture.componentRef.setInput('data', [mockWeatherData[0]]);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const ticks = component.xTickValues();

      expect(ticks.length).toBe(1);
    });

    it('should include first and last data points', () => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const ticks = component.xTickValues();
      const chartData = component.chartData();

      expect(ticks[0]).toBe(chartData[0].x.getTime());
      expect(ticks[ticks.length - 1]).toBe(chartData[chartData.length - 1].x.getTime());
    });

    it('should return all points for small datasets', () => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const ticks = component.xTickValues();

      // Should have all 3 points since data.length <= 10
      expect(ticks.length).toBe(3);
    });

    it('should limit ticks to maxTicks for large datasets', () => {
      const largeDataset: WeatherMeasurement[] = [];
      for (let i = 0; i < 50; i++) {
        largeDataset.push({
          ...mockWeatherData[0],
          measuredAt: new Date(2024, 0, i + 1, 12, 0, 0)
        });
      }

      fixture.componentRef.setInput('data', largeDataset);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const ticks = component.xTickValues();

      // Should have at most 10 ticks (maxTicks)
      expect(ticks.length).toBeLessThanOrEqual(10);
    });
  });

  describe('formatting functions', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();
    });

    it('should format dates as DD/MM', () => {
      const date = new Date('2024-01-15T12:00:00');
      const formatted = component.dateFormat(date);

      expect(formatted).toBe('15/01');
    });

    it('should format date from timestamp', () => {
      const timestamp = new Date('2024-03-05T12:00:00').getTime();
      const formatted = component.dateFormat(timestamp);

      expect(formatted).toBe('05/03');
    });

    it('should format temperature with 1 decimal and °C suffix', () => {
      const formatted = component.yTickFormat(25.567);

      expect(formatted).toBe('25.6°C');
    });

    it('should handle integer temperatures', () => {
      const formatted = component.yTickFormat(20);

      expect(formatted).toBe('20.0°C');
    });

    it('should handle Date in yTickFormat by returning 0.0°C', () => {
      const formatted = component.yTickFormat(new Date());

      expect(formatted).toBe('0.0°C');
    });
  });

  describe('crosshairTemplate', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();
    });

    it('should generate HTML template with date and temperature', () => {
      const dataPoint = { x: new Date('2024-01-15T12:00:00'), y: 23.5 };
      const html = component.crosshairTemplate(dataPoint);

      expect(html).toContain('15/01/2024');
      expect(html).toContain('23.5°C');
      expect(html).toContain('Température');
    });

    it('should handle Date object in x', () => {
      const dataPoint = { x: new Date('2024-12-25T12:00:00'), y: 18.2 };
      const html = component.crosshairTemplate(dataPoint);

      expect(html).toContain('25/12/2024');
      expect(html).toContain('18.2°C');
    });

    it('should include styling classes', () => {
      const dataPoint = { x: new Date('2024-01-15T12:00:00'), y: 23.5 };
      const html = component.crosshairTemplate(dataPoint);

      expect(html).toContain('bg-base-100');
      expect(html).toContain('rounded-lg');
      expect(html).toContain('shadow-lg');
    });
  });

  describe('accessor functions', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();
    });

    it('should extract x value', () => {
      const testData = { x: new Date('2024-01-01'), y: 25.5 };
      const xValue = component.x(testData);

      expect(xValue).toEqual(testData.x);
    });

    it('should extract y value', () => {
      const testData = { x: new Date('2024-01-01'), y: 25.5 };
      const yValue = component.y(testData);

      expect(yValue).toBe(25.5);
    });
  });

  describe('inputs', () => {
    it('should display city name in title', () => {
      fixture.componentRef.setInput('data', []);
      fixture.componentRef.setInput('cityName', 'Montpellier');
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Montpellier');
    });

    it('should use custom height when provided', () => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.componentRef.setInput('height', 400);
      fixture.detectChanges();

      expect(component.height()).toBe(400);
    });

    it('should use default height of 300 when not provided', () => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      expect(component.height()).toBe(300);
    });
  });

  describe('empty state', () => {
    it('should show "no data" message when data is empty', () => {
      fixture.componentRef.setInput('data', []);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      expect(compiled.textContent).toContain('Aucune donnée de température disponible');
    });

    it('should not show chart when data is empty', () => {
      fixture.componentRef.setInput('data', []);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      const chart = compiled.querySelector('vis-xy-container');
      expect(chart).toBeNull();
    });

    it('should show chart when data is provided', () => {
      fixture.componentRef.setInput('data', mockWeatherData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const compiled = fixture.nativeElement as HTMLElement;
      const chart = compiled.querySelector('vis-xy-container');
      expect(chart).toBeTruthy();
    });
  });
});
