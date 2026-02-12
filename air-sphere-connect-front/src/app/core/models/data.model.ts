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
  unit: string;
  station?: string;  // Nom de la station (string depuis le backend)
  dataSource?: string;  // "exact", "areaCode", "department", "neighboring_departments"
  sourceCities?: string;  // Villes sources (ex: "Toulouse (Haute-Garonne), Montpellier (Hérault)")
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

export interface AirQualityData {
  cityId: number;
  cityName: string;
  postalCode?: string;
  areaCode?: string;
  population?: number;
  qualityIndex?: number;
  qualityLabel?: string;
  qualityColor?: string;
  indexMeasuredAt?: string;
  alertMessage?: string;
  pm10?: number;
  pm25?: number;
  no2?: number;
  o3?: number;
  so2?: number;
  pollutantsMeasuredAt?: string;
  latestMeasurement?: AirQualityMeasurement;
  latestIndex?: AirQualityIndex;
  measurementHistory?: AirQualityMeasurement[];
  indexHistory?: AirQualityIndex[];
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
  airQuality: AirQualityData;
  populationHistory: PopulationData[];
}
