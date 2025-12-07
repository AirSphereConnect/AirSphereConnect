import { Component, computed, effect, input, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { CityHistoryData } from '../../../../core/models/city.model';
import { translateWeatherMessage } from '../../../../shared/utils/weather-translator.util';
import {Button} from '../../../../shared/components/ui/button/button';

@Component({
  selector: 'app-weather-history',
  standalone: true,
  imports: [DatePipe, Button],
  templateUrl: './weather-history.html',
  styleUrls: ['./weather-history.scss']
})
export class WeatherHistory {
  historyData = input.required<CityHistoryData>();
  startDate = input<string>('');
  endDate = input<string>('');

  // Pagination
  currentPage = signal(1);
  itemsPerPage = 10;

  constructor() {
    // Réinitialiser la page à 1 quand les filtres de date changent
    effect(() => {
      this.startDate();
      this.endDate();
      this.currentPage.set(1);
    });
  }

  // Données filtrées par période ET avec données météo
  allFilteredData = computed(() => {
    const snapshots = this.historyData().dailySnapshots;
    const start = this.startDate();
    const end = this.endDate();

    let filtered = snapshots;

    // Filtre par date si nécessaire
    if (start || end) {
      filtered = snapshots.filter(snapshot => {
        const date = new Date(snapshot.date);
        const startDateObj = start ? new Date(start) : null;
        const endDateObj = end ? new Date(end + 'T23:59:59') : null;

        if (startDateObj && date < startDateObj) return false;
        return !(endDateObj && date > endDateObj);
      });
    }

    // Ne garder QUE les snapshots avec données météo pour la pagination
    filtered = filtered.filter(snapshot => snapshot.weather);

    // Trier par date décroissante
    return filtered.sort((a, b) => b.date.getTime() - a.date.getTime());
  });

  // Données paginées pour l'affichage
  filteredData = computed(() => {
    const allData = this.allFilteredData();
    const page = this.currentPage();
    const start = (page - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return allData.slice(start, end);
  });

  // Nombre total de pages
  totalPages = computed(() => {
    return Math.ceil(this.allFilteredData().length / this.itemsPerPage);
  });

  // Nombre de snapshots avec données météo
  hasWeatherData = computed(() => {
    return this.allFilteredData().some(s => s.weather);
  });

  // Vérifie si on a des données mais aucune météo
  hasOnlyAirQualityData = computed(() => {
    return this.allFilteredData().length > 0 && !this.hasWeatherData();
  });

  // Fonction pour obtenir la direction du vent en texte
  getWindDirection(degrees: number | undefined): string {
    if (degrees === undefined) return 'Non mesuré';

    const directions = ['N', 'NE', 'E', 'SE', 'S', 'SO', 'O', 'NO'];
    const index = Math.round(degrees / 45) % 8;
    return directions[index];
  }

  // Fonction pour formater la température avec unité
  formatTemperature(temp: number | undefined): string {
    if (temp === undefined) return 'Non mesuré';
    return `${temp.toFixed(1)}°C`;
  }

  // Fonction pour formater l'humidité
  formatHumidity(humidity: number | undefined): string {
    if (humidity === undefined) return 'Non mesuré';
    return `${humidity}%`;
  }

  // Fonction pour formater la pression
  formatPressure(pressure: number | undefined): string {
    if (pressure === undefined) return 'Non mesuré';
    return `${pressure} hPa`;
  }

  // Fonction pour formater la vitesse du vent
  formatWindSpeed(speed: number | undefined): string {
    if (speed === undefined) return 'Non mesuré';
    return `${speed.toFixed(1)} m/s`;
  }

  // Fonction pour traduire le message météo
  translateWeatherMessage = translateWeatherMessage;

  // Fonction pour extraire le code de l'icône météo du message JSON
  getWeatherIcon(message: string | undefined): string | null {
    if (!message) return null;

    try {
      let weatherData: any;

      // Si c'est déjà un objet/array, l'utiliser directement
      if (typeof message === 'object') {
        weatherData = message;
      } else {
        // Sinon, c'est une string JSON à parser
        weatherData = JSON.parse(message);
      }

      // Convertir en array si nécessaire
      const weatherArray = Array.isArray(weatherData) ? weatherData : [weatherData];

      if (weatherArray.length === 0) {
        return null;
      }

      const weather = weatherArray[0];
      return weather.icon || null;
    } catch {
      // Invalid JSON format, return null
      return null;
    }
  }

  // Fonction pour obtenir l'URL de l'icône OpenWeatherMap
  getWeatherIconUrl(icon: string): string {
    return `https://openweathermap.org/img/wn/${icon}@2x.png`;
  }

  // Navigation pagination
  nextPage() {
    const total = this.totalPages();
    if (this.currentPage() < total) {
      this.currentPage.set(this.currentPage() + 1);
    }
  }

  prevPage() {
    if (this.currentPage() > 1) {
      this.currentPage.set(this.currentPage() - 1);
    }
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages()) {
      this.currentPage.set(page);
    }
  }
}
