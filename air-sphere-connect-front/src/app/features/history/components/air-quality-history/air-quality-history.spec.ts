import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AirQualityHistory } from './air-quality-history';
import { CityHistoryData, CityDailySnapshot, City } from '../../../../core/models/city.model';

describe('AirQualityHistory', () => {
  let component: AirQualityHistory;
  let fixture: ComponentFixture<AirQualityHistory>;

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

  const createSnapshot = (date: string, hasAirData: boolean): CityDailySnapshot => ({
    date: new Date(date),
    weather: null,
    airMeasurement: hasAirData ? {
      measuredAt: date,
      pm25: 15.5,
      pm10: 25.3,
      no2: 30.2,
      o3: 45.1,
      so2: 5.5,
      unit: 'µg/m³'
    } : null,
    airIndex: hasAirData ? {
      qualityIndex: 3,
      qualityLabel: 'Moyen',
      qualityColor: 'orange',
      measuredAt: date,
      alert: false,
      areaName: 'Paris'
    } : null
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
      imports: [AirQualityHistory]
    }).compileComponents();

    fixture = TestBed.createComponent(AirQualityHistory);
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

    it('should filter data by start date', () => {
      fixture.componentRef.setInput('startDate', '2024-01-13');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered.length).toBe(3); // Jan 13, 14, 15
      expect(filtered[0].date.getDate()).toBe(15); // Sorted descending
    });

    it('should filter data by end date', () => {
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '2024-01-13');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered.length).toBe(3); // Jan 11, 12, 13
      expect(filtered[0].date.getDate()).toBe(13); // Latest within range
    });

    it('should filter data by date range', () => {
      fixture.componentRef.setInput('startDate', '2024-01-12');
      fixture.componentRef.setInput('endDate', '2024-01-14');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered.length).toBe(3); // Jan 12, 13, 14
    });

    it('should show all data when no dates provided', () => {
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered.length).toBe(5);
    });

    it('should sort data by date descending', () => {
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      expect(filtered[0].date.getDate()).toBe(15); // Most recent first
      expect(filtered[4].date.getDate()).toBe(11); // Oldest last
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

    it('should show correct items per page', () => {
      const paginated = component.filteredData();

      expect(paginated.length).toBe(10); // itemsPerPage = 10
    });

    it('should calculate total pages correctly', () => {
      const total = component.totalPages();

      expect(total).toBe(3); // 25 items / 10 per page = 3 pages
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

    it('should not go to page 0', () => {
      component.prevPage();

      expect(component.currentPage()).toBe(1);
    });

    it('should not go beyond last page', () => {
      component.currentPage.set(3);
      component.nextPage();

      expect(component.currentPage()).toBe(3);
    });

    it('should go to specific page', () => {
      component.goToPage(2);

      expect(component.currentPage()).toBe(2);
    });

    it('should not go to invalid page number', () => {
      component.goToPage(10);

      expect(component.currentPage()).toBe(1); // Stays on current
    });

    it('should show correct items on page 2', () => {
      component.currentPage.set(2);
      const paginated = component.filteredData();

      expect(paginated.length).toBe(10);
      // Verify different items than page 1
    });

    it('should show remaining items on last page', () => {
      component.currentPage.set(3);
      const paginated = component.filteredData();

      expect(paginated.length).toBe(5); // 25 % 10 = 5 remaining
    });
  });

  describe('air quality data detection', () => {
    it('should detect when air quality data exists', () => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();

      expect(component.hasAirQualityData()).toBe(true);
    });

    it('should detect when only weather data exists', () => {
      const weatherOnlyData: CityHistoryData = {
        city: mockCity,
        dailySnapshots: [createSnapshot('2024-01-15T12:00:00', false)]
      };

      fixture.componentRef.setInput('historyData', weatherOnlyData);
      fixture.componentRef.setInput('startDate', '');
      fixture.componentRef.setInput('endDate', '');
      fixture.detectChanges();

      expect(component.hasAirQualityData()).toBe(false);
      expect(component.hasOnlyWeatherData()).toBe(true);
    });

    it('should return false for empty data', () => {
      const emptyData: CityHistoryData = {
        city: mockCity,
        dailySnapshots: []
      };

      fixture.componentRef.setInput('historyData', emptyData);
      fixture.detectChanges();

      expect(component.hasAirQualityData()).toBe(false);
      expect(component.hasOnlyWeatherData()).toBe(false);
    });
  });

  describe('quality badge class', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.detectChanges();
    });

    it('should return success badge for good quality (1-2)', () => {
      expect(component.getQualityBadgeClass(1)).toBe('badge-success');
      expect(component.getQualityBadgeClass(2)).toBe('badge-success');
    });

    it('should return warning badge for medium quality (3-4)', () => {
      expect(component.getQualityBadgeClass(3)).toBe('badge-warning');
      expect(component.getQualityBadgeClass(4)).toBe('badge-warning');
    });

    it('should return error badge for poor quality (5+)', () => {
      expect(component.getQualityBadgeClass(5)).toBe('badge-error');
      expect(component.getQualityBadgeClass(6)).toBe('badge-error');
    });

    it('should return neutral badge for undefined quality', () => {
      expect(component.getQualityBadgeClass(undefined)).toBe('badge-neutral');
    });

    it('should return neutral badge for null quality', () => {
      expect(component.getQualityBadgeClass(0)).toBe('badge-neutral');
    });
  });

  describe('pagination with filtered data', () => {
    beforeEach(() => {
      const largeDataset: CityHistoryData = {
        city: mockCity,
        dailySnapshots: Array.from({ length: 30 }, (_, i) =>
          createSnapshot(`2024-01-${String(i + 1).padStart(2, '0')}T12:00:00`, true)
        )
      };
      fixture.componentRef.setInput('historyData', largeDataset);
    });

    it('should paginate filtered results', () => {
      fixture.componentRef.setInput('startDate', '2024-01-10');
      fixture.componentRef.setInput('endDate', '2024-01-20');
      fixture.detectChanges();

      const allFiltered = component.allFilteredData();
      const paginated = component.filteredData();

      expect(allFiltered.length).toBe(11); // Jan 10-20
      expect(paginated.length).toBe(10); // First page
    });

    it('should calculate total pages based on filtered data', () => {
      fixture.componentRef.setInput('startDate', '2024-01-01');
      fixture.componentRef.setInput('endDate', '2024-01-15');
      fixture.detectChanges();

      const total = component.totalPages();

      expect(total).toBe(2); // 15 items / 10 per page = 2 pages
    });

    it('should reset pagination context when filter changes', () => {
      component.currentPage.set(2);
      fixture.componentRef.setInput('startDate', '2024-01-01');
      fixture.componentRef.setInput('endDate', '2024-01-05');
      fixture.detectChanges();

      // Page 2 may not exist after filtering
      const paginated = component.filteredData();
      expect(paginated).toBeDefined();
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

      const filtered = component.filteredData();

      expect(filtered.length).toBe(1);
      expect(component.totalPages()).toBe(1);
    });

    it('should handle end date including full day', () => {
      fixture.componentRef.setInput('historyData', mockHistoryData);
      fixture.componentRef.setInput('startDate', '2024-01-13');
      fixture.componentRef.setInput('endDate', '2024-01-13');
      fixture.detectChanges();

      const filtered = component.allFilteredData();

      // Should include Jan 13 even though times might differ
      expect(filtered.some(s => s.date.getDate() === 13)).toBe(true);
    });
  });
});
