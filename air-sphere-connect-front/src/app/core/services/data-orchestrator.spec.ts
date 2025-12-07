import { TestBed, fakeAsync, flush } from '@angular/core/testing';
import { of } from 'rxjs';
import { DataOrchestratorService } from './data-orchestrator';
import { CityService } from './city';
import { WeatherService } from './weather';
import { AirQualityService } from './air-quality';
import { PopulationService } from './population';
import { City } from '../models/city.model';
import {
  WeatherMeasurement,
  AirQualityComplete,
  PopulationData,
  AirQualityMeasurement,
  AirQualityIndex
} from '../models/data.model';

describe('DataOrchestratorService', () => {
  let service: DataOrchestratorService;
  let mockCityService: jasmine.SpyObj<CityService>;
  let mockWeatherService: jasmine.SpyObj<WeatherService>;
  let mockAirQualityService: jasmine.SpyObj<AirQualityService>;
  let mockPopulationService: jasmine.SpyObj<PopulationService>;

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

  const mockWeatherHistory: WeatherMeasurement[] = [
    {
      id: 1,
      temperature: 25.5,
      humidity: 65,
      pressure: 1013,
      windSpeed: 15.2,
      windDirection: 180,
      measuredAt: new Date('2024-01-01T12:00:00'),
      message: '[{"main":"Clear","description":"clear sky","icon":"01d"}]'
    },
    {
      id: 2,
      temperature: 23.8,
      humidity: 70,
      pressure: 1015,
      windSpeed: 12.5,
      windDirection: 160,
      measuredAt: new Date('2024-01-02T12:00:00'),
      message: '[{"main":"Clouds","description":"few clouds","icon":"02d"}]'
    }
  ];

  const mockAirQualityMeasurement: AirQualityMeasurement = {
    measuredAt: '2024-01-01T12:00:00',
    pm25: 15.5,
    pm10: 25.3,
    no2: 30.2,
    o3: 45.1,
    so2: 5.5,
    co: 200,
    unit: 'µg/m³'
  };

  const mockAirQualityIndex: AirQualityIndex = {
    qualityIndex: 3,
    qualityLabel: 'Moyen',
    qualityColor: 'orange',
    measuredAt: '2024-01-01T12:00:00',
    alert: false,
    areaName: 'Paris'
  };

  const mockAirQualityComplete: AirQualityComplete = {
    latestMeasurement: mockAirQualityMeasurement,
    latestIndex: mockAirQualityIndex,
    measurementHistory: [mockAirQualityMeasurement],
    indexHistory: [mockAirQualityIndex]
  };

  const mockPopulationHistory: PopulationData[] = [
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
    }
  ];

  beforeEach(() => {
    mockCityService = jasmine.createSpyObj('CityService', ['getByName']);
    mockWeatherService = jasmine.createSpyObj('WeatherService', ['getHistory', 'getLatest']);
    mockAirQualityService = jasmine.createSpyObj('AirQualityService', [
      'getComplete',
      'getLatestIndex',
      'getLatestMeasurement'
    ]);
    mockPopulationService = jasmine.createSpyObj('PopulationService', ['getHistory']);

    TestBed.configureTestingModule({
      providers: [
        DataOrchestratorService,
        { provide: CityService, useValue: mockCityService },
        { provide: WeatherService, useValue: mockWeatherService },
        { provide: AirQualityService, useValue: mockAirQualityService },
        { provide: PopulationService, useValue: mockPopulationService }
      ]
    });

    service = TestBed.inject(DataOrchestratorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('loadDashboardData', () => {
    it('should orchestrate and load complete dashboard data for a city', (done) => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getHistory.and.returnValue(of(mockWeatherHistory));
      mockAirQualityService.getComplete.and.returnValue(of(mockAirQualityComplete));
      mockPopulationService.getHistory.and.returnValue(of(mockPopulationHistory));

      service.loadDashboardData('Paris').subscribe(dashboardData => {
        expect(dashboardData.city).toEqual(mockCity);
        expect(dashboardData.weatherHistory).toEqual(mockWeatherHistory);
        expect(dashboardData.airQuality).toEqual(mockAirQualityComplete);
        expect(dashboardData.populationHistory).toEqual(mockPopulationHistory);

        expect(mockCityService.getByName).toHaveBeenCalledWith('Paris');
        expect(mockWeatherService.getHistory).toHaveBeenCalledWith(1);
        expect(mockAirQualityService.getComplete).toHaveBeenCalledWith('Paris');
        expect(mockPopulationService.getHistory).toHaveBeenCalledWith('Paris');

        done();
      });
    });

    it('should use city ID for weather service', (done) => {
      const cityWithDifferentId: City = { ...mockCity, id: 999 };
      mockCityService.getByName.and.returnValue(of(cityWithDifferentId));
      mockWeatherService.getHistory.and.returnValue(of([]));
      mockAirQualityService.getComplete.and.returnValue(of(mockAirQualityComplete));
      mockPopulationService.getHistory.and.returnValue(of([]));

      service.loadDashboardData('TestCity').subscribe(() => {
        expect(mockWeatherService.getHistory).toHaveBeenCalledWith(999);
        done();
      });
    });

    it('should handle empty histories', (done) => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getHistory.and.returnValue(of([]));
      mockAirQualityService.getComplete.and.returnValue(of({
        latestMeasurement: mockAirQualityMeasurement,
        latestIndex: mockAirQualityIndex,
        measurementHistory: [],
        indexHistory: []
      }));
      mockPopulationService.getHistory.and.returnValue(of([]));

      service.loadDashboardData('Paris').subscribe(dashboardData => {
        expect(dashboardData.weatherHistory.length).toBe(0);
        expect(dashboardData.airQuality.measurementHistory.length).toBe(0);
        expect(dashboardData.populationHistory.length).toBe(0);
        done();
      });
    });
  });

  describe('loadFavoriteCitySnapshot', () => {
    it('should load snapshot data for a favorite city', (done) => {
      const latestWeather: WeatherMeasurement = mockWeatherHistory[0];

      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getLatest.and.returnValue(of(latestWeather));
      mockAirQualityService.getLatestIndex.and.returnValue(of(mockAirQualityIndex));
      mockAirQualityService.getLatestMeasurement.and.returnValue(of(mockAirQualityMeasurement));

      service.loadFavoriteCitySnapshot('Paris').subscribe(favoriteData => {
        expect(favoriteData.city).toEqual(mockCity);
        expect(favoriteData.weather).toEqual(latestWeather);
        expect(favoriteData.airQualityIndex).toEqual(mockAirQualityIndex);
        expect(favoriteData.airQualityMeasurement).toEqual(mockAirQualityMeasurement);

        expect(mockCityService.getByName).toHaveBeenCalledWith('Paris');
        expect(mockWeatherService.getLatest).toHaveBeenCalledWith(1);
        expect(mockAirQualityService.getLatestIndex).toHaveBeenCalledWith('Paris');
        expect(mockAirQualityService.getLatestMeasurement).toHaveBeenCalledWith('Paris');

        done();
      });
    });

    it('should handle null weather data', (done) => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getLatest.and.returnValue(of(null));
      mockAirQualityService.getLatestIndex.and.returnValue(of(mockAirQualityIndex));
      mockAirQualityService.getLatestMeasurement.and.returnValue(of(mockAirQualityMeasurement));

      service.loadFavoriteCitySnapshot('Paris').subscribe(favoriteData => {
        expect(favoriteData.weather).toBeNull();
        expect(favoriteData.city).toEqual(mockCity);
        done();
      });
    });

    it('should handle null air quality data', (done) => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getLatest.and.returnValue(of(mockWeatherHistory[0]));
      mockAirQualityService.getLatestIndex.and.returnValue(of(null));
      mockAirQualityService.getLatestMeasurement.and.returnValue(of(null));

      service.loadFavoriteCitySnapshot('Paris').subscribe(favoriteData => {
        expect(favoriteData.airQualityIndex).toBeNull();
        expect(favoriteData.airQualityMeasurement).toBeNull();
        done();
      });
    });
  });

  describe('loadMultipleFavoritesSnapshots', () => {
    it('should load snapshots for multiple cities', (done) => {
      const city2: City = { ...mockCity, id: 2, name: 'Lyon' };

      mockCityService.getByName.and.returnValues(
        of(mockCity),
        of(city2)
      );
      mockWeatherService.getLatest.and.returnValue(of(mockWeatherHistory[0]));
      mockAirQualityService.getLatestIndex.and.returnValue(of(mockAirQualityIndex));
      mockAirQualityService.getLatestMeasurement.and.returnValue(of(mockAirQualityMeasurement));

      service.loadMultipleFavoritesSnapshots(['Paris', 'Lyon']).subscribe(snapshots => {
        expect(snapshots.length).toBe(2);
        expect(snapshots[0].city.name).toBe('Paris');
        expect(snapshots[1].city.name).toBe('Lyon');

        expect(mockCityService.getByName).toHaveBeenCalledTimes(2);
        expect(mockCityService.getByName).toHaveBeenCalledWith('Paris');
        expect(mockCityService.getByName).toHaveBeenCalledWith('Lyon');

        done();
      });
    });

    it('should handle empty city list', fakeAsync(() => {
      service.loadMultipleFavoritesSnapshots([]).subscribe(snapshots => {
        expect(snapshots.length).toBe(0);
        expect(Array.isArray(snapshots)).toBe(true);
      });

      flush();
    }));

    it('should handle single city', (done) => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getLatest.and.returnValue(of(mockWeatherHistory[0]));
      mockAirQualityService.getLatestIndex.and.returnValue(of(mockAirQualityIndex));
      mockAirQualityService.getLatestMeasurement.and.returnValue(of(mockAirQualityMeasurement));

      service.loadMultipleFavoritesSnapshots(['Paris']).subscribe(snapshots => {
        expect(snapshots.length).toBe(1);
        expect(snapshots[0].city.name).toBe('Paris');
        done();
      });
    });
  });

  describe('loadCityHistoryTable', () => {
    it('should load and merge historical data into daily snapshots', (done) => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getHistory.and.returnValue(of(mockWeatherHistory));
      mockAirQualityService.getComplete.and.returnValue(of(mockAirQualityComplete));

      service.loadCityHistoryTable('Paris').subscribe(historyData => {
        expect(historyData.city).toEqual(mockCity);
        expect(historyData.dailySnapshots).toBeDefined();
        expect(Array.isArray(historyData.dailySnapshots)).toBe(true);
        expect(historyData.dailySnapshots.length).toBeGreaterThan(0);

        // Verify daily snapshots structure
        const snapshot = historyData.dailySnapshots[0];
        expect(snapshot.date).toBeInstanceOf(Date);
        expect(snapshot.weather !== undefined).toBe(true);
        expect(snapshot.airMeasurement !== undefined).toBe(true);
        expect(snapshot.airIndex !== undefined).toBe(true);

        done();
      });
    });

    it('should cache historical data on first call', fakeAsync(() => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getHistory.and.returnValue(of(mockWeatherHistory));
      mockAirQualityService.getComplete.and.returnValue(of(mockAirQualityComplete));

      // First call
      service.loadCityHistoryTable('Paris').subscribe(() => {
        // Second call should use cache
        service.loadCityHistoryTable('Paris').subscribe(() => {
          // Service methods should only be called once due to caching
          expect(mockCityService.getByName).toHaveBeenCalledTimes(1);
          expect(mockWeatherService.getHistory).toHaveBeenCalledTimes(1);
          expect(mockAirQualityService.getComplete).toHaveBeenCalledTimes(1);
        });
      });

      flush();
    }));

    it('should sort daily snapshots by date descending', (done) => {
      const weatherWithMultipleDates: WeatherMeasurement[] = [
        { ...mockWeatherHistory[0], measuredAt: new Date('2024-01-03T12:00:00') },
        { ...mockWeatherHistory[0], measuredAt: new Date('2024-01-01T12:00:00') },
        { ...mockWeatherHistory[0], measuredAt: new Date('2024-01-02T12:00:00') }
      ];

      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getHistory.and.returnValue(of(weatherWithMultipleDates));
      mockAirQualityService.getComplete.and.returnValue(of(mockAirQualityComplete));

      service.loadCityHistoryTable('Paris').subscribe(historyData => {
        const snapshots = historyData.dailySnapshots;
        expect(snapshots.length).toBeGreaterThan(1);

        // Verify descending order (newest first)
        for (let i = 1; i < snapshots.length; i++) {
          expect(snapshots[i - 1].date.getTime()).toBeGreaterThanOrEqual(snapshots[i].date.getTime());
        }

        done();
      });
    });
  });

  describe('clearHistoryCache', () => {
    it('should clear cache for specific city', fakeAsync(() => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getHistory.and.returnValue(of(mockWeatherHistory));
      mockAirQualityService.getComplete.and.returnValue(of(mockAirQualityComplete));

      // Load data to populate cache
      service.loadCityHistoryTable('Paris').subscribe(() => {
        // Clear cache
        service.clearHistoryCache('Paris');

        // Load again - should call services again
        service.loadCityHistoryTable('Paris').subscribe(() => {
          expect(mockCityService.getByName).toHaveBeenCalledTimes(2);
        });
      });

      flush();
    }));

    it('should clear entire cache when no city specified', fakeAsync(() => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getHistory.and.returnValue(of(mockWeatherHistory));
      mockAirQualityService.getComplete.and.returnValue(of(mockAirQualityComplete));

      // Load data for Paris
      service.loadCityHistoryTable('Paris').subscribe(() => {
        // Clear entire cache
        service.clearHistoryCache();

        // Load again - should call services again
        service.loadCityHistoryTable('Paris').subscribe(() => {
          expect(mockCityService.getByName).toHaveBeenCalledTimes(2);
        });
      });

      flush();
    }));
  });

  describe('loadAdminDashboardData', () => {
    it('should call loadDashboardData and return same result', (done) => {
      mockCityService.getByName.and.returnValue(of(mockCity));
      mockWeatherService.getHistory.and.returnValue(of(mockWeatherHistory));
      mockAirQualityService.getComplete.and.returnValue(of(mockAirQualityComplete));
      mockPopulationService.getHistory.and.returnValue(of(mockPopulationHistory));

      service.loadAdminDashboardData('Paris').subscribe(dashboardData => {
        expect(dashboardData.city).toEqual(mockCity);
        expect(dashboardData.weatherHistory).toEqual(mockWeatherHistory);
        expect(dashboardData.airQuality).toEqual(mockAirQualityComplete);
        expect(dashboardData.populationHistory).toEqual(mockPopulationHistory);
        done();
      });
    });
  });
});
