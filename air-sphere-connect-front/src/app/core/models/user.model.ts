
export interface Favorite {
  id: number;
  cityName: string;
  createdAt: string;
  updatedAt: string;
  favoriteCategory: string;
}

export interface Alerts {
  id: number;
  cityName: string;
  createdAt: string;
  updatedAt: string;
  isEnabled: boolean;
}

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
  address: {
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
