export interface City {
  id: number;
  name: string;
  postalCode: string;
  inseeCode: string;
  areaCode: string;
  latitude?: number;
  longitude?: number;
  population?: number;
  departmentName?: string;
}

// Import des types nécessaires pour les interfaces city-related
import { AirQualityIndex, AirQualityMeasurement, WeatherMeasurement } from './data.model';

export interface FavoriteCityData {
  city: City;
  weather: WeatherMeasurement | null;
  airQualityIndex: AirQualityIndex | null;
  airQualityMeasurement: AirQualityMeasurement | null;
}

export interface CityDailySnapshot {
  date: Date;
  weather: WeatherMeasurement | null;
  airMeasurement: AirQualityMeasurement | null;
  airIndex: AirQualityIndex | null;
}

export interface CityHistoryData {
  city: City;
  dailySnapshots: CityDailySnapshot[];
}
