import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PollutantsChart } from './pollutants-chart';
import { AirQualityMeasurement } from '../../../../core/models/data.model';

describe('PollutantsChart', () => {
  let component: PollutantsChart;
  let fixture: ComponentFixture<PollutantsChart>;

  const mockAirQualityData: AirQualityMeasurement[] = [
    {
      measuredAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
      pm25: 15.5,
      pm10: 25.3,
      no2: 30.2,
      o3: 45.1,
      so2: 5.5,
      unit: 'µg/m³'
    },
    {
      measuredAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
      pm25: 12.8,
      pm10: 22.1,
      no2: 28.5,
      o3: 42.3,
      so2: 4.8,
      unit: 'µg/m³'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PollutantsChart]
    }).compileComponents();

    fixture = TestBed.createComponent(PollutantsChart);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.componentRef.setInput('data', []);
    fixture.componentRef.setInput('cityName', 'Test City');
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  describe('chartData computation', () => {
    it('should compute chart data from air quality measurements', () => {
      fixture.componentRef.setInput('data', mockAirQualityData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData.length).toBe(2);
      expect(chartData[0].pm25).toBeDefined();
      expect(chartData[0].pm10).toBeDefined();
    });

    it('should group measurements on same day and calculate average', () => {
      const today = new Date();
      const sameDayData: AirQualityMeasurement[] = [
        { ...mockAirQualityData[0], pm25: 10.0, measuredAt: new Date(today.getFullYear(), today.getMonth(), today.getDate(), 8, 0, 0).toISOString() },
        { ...mockAirQualityData[0], pm25: 20.0, measuredAt: new Date(today.getFullYear(), today.getMonth(), today.getDate(), 12, 0, 0).toISOString() },
        { ...mockAirQualityData[0], pm25: 30.0, measuredAt: new Date(today.getFullYear(), today.getMonth(), today.getDate(), 16, 0, 0).toISOString() }
      ];

      fixture.componentRef.setInput('data', sameDayData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData.length).toBe(1);
      expect(chartData[0].pm25).toBe(20.0);
    });

    it('should return empty array when no data provided', () => {
      fixture.componentRef.setInput('data', []);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      const chartData = component.chartData();

      expect(chartData.length).toBe(0);
    });
  });

  describe('alert system', () => {
    it('should detect PM2.5 threshold exceeded', () => {
      const highPM25Data: AirQualityMeasurement[] = [
        { ...mockAirQualityData[0], pm25: 40.0 }
      ];

      fixture.componentRef.setInput('data', highPM25Data);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      expect(component.hasAlerts()).toBe(true);
      expect(component.alertMessage()).toContain('PM2.5');
    });

    it('should not have alerts when all values below thresholds', () => {
      fixture.componentRef.setInput('data', mockAirQualityData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      expect(component.hasAlerts()).toBe(false);
      expect(component.alertMessage()).toBe('');
    });
  });

  describe('period selection', () => {
    it('should start with 7 days as default period', () => {
      fixture.componentRef.setInput('data', mockAirQualityData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      expect(component.selectedPeriod()).toBe('7days');
    });

    it('should update period when selectPeriod is called', () => {
      fixture.componentRef.setInput('data', mockAirQualityData);
      fixture.componentRef.setInput('cityName', 'Paris');
      fixture.detectChanges();

      component.selectPeriod('15days');

      expect(component.selectedPeriod()).toBe('15days');
    });
  });
});
