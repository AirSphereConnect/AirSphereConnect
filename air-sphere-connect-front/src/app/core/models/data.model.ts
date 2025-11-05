export interface WeatherMeasurement {
  id: number;
  temperature: number;
  humidity: number;
  pressure: number;
  windSpeed: number;
  windDirection: number;
  measuredAt: Date;
  message?: string;
}

export interface AirQualityMeasurement {
  measuredAt: string;
  pm25: number;
  pm10: number;
  no2: number;
  o3: number;
  so2: number;
  co: number;
  unit: string;
  station?: {
    id: number;
    name: string;
    latitude: number;
    longitude: number;
  };
}

export interface AirQualityIndex {
  qualityIndex: number;
  qualityLabel: string;
  qualityColor: string;
  measuredAt: string;
  alert: boolean;
  alertMessage?: string;
  areaName: string;
}

export interface AirQualityComplete {
  latestMeasurement: AirQualityMeasurement;
  latestIndex: AirQualityIndex;
  measurementHistory: AirQualityMeasurement[];
  indexHistory: AirQualityIndex[];
}

export interface PopulationData {
  id?: number;
  cityName?: string;
  population: number;
  year: number;
  source: string;
}

// === Compositions pour pages ===

import { City } from './city.model';

export interface DashboardData {
  city: City;
  weatherHistory: WeatherMeasurement[];
  airQuality: AirQualityComplete;
  populationHistory: PopulationData[];
}
