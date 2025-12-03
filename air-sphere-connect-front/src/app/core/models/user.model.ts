
export interface Favorite {
  id: number;
  cityName: string;
  createdAt: string;
  updatedAt: string;
  selectWeather: boolean;
  selectAirQuality: boolean;
  selectPopulation: boolean;
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

// Payloads pour les requêtes
export interface RegisterPayload {
  username: string;
  email: string;
  address: {
    street: string;
    city: {
      id: number;
    };
  };
}

export interface UpdateUserPayload {
  username?: string;
  email?: string;
  password?: string;
}

export interface UpdateAddressPayload {
  street: string;
  city: {
    id: number;
  };
}

export interface AddFavoritePayload {
  selectWeather: boolean;
  selectAirQuality: boolean;
  selectPopulation: boolean;
  cityId: number | null;
}

export interface AddAlertPayload {
  enabled: boolean;
  cityId: number | null;
}
