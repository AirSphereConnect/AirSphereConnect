
export interface Favorite {
  id: number;
  cityName: string;
  createdAt: string;
  updatedAt: string;
  weather: boolean;
  airQuality: boolean;
  population: boolean;
}

export interface Alerts {
  id: number;
  cityName: string;
  createdAt: string;
  updatedAt: string;
  enabled: boolean;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  address: {
    id: number;
    street: string;
    city: {
      name: string;
      postalCode: string;
    };
  };
  favorites: Favorite[];
  alerts: Alerts[];
}

export interface UserProfileResponse {
  role: string;
  user: User;
}
