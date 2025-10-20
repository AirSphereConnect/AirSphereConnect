import {City} from './city.model';
import {Region} from './region.model';

export interface Department {
  id: number;
  name: string;
  code: string;
  cities: City[];
  regionName: string;
}
