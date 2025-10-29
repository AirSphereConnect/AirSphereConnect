export interface City {
  id: number;
  name: string;
  postalCode: string;
  inseeCode: string;
  latitude?: number;
  longitude?: number;
  population?: number;
  department?: {
    id: number;
    name: string;
    code: string;
  };
}
