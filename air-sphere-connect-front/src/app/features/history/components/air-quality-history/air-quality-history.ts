import {Component, computed, input, signal} from '@angular/core';
import {DatePipe, NgClass} from '@angular/common';
import {CityHistoryData} from '../../../../core/models/city.model';
import {Button} from '../../../../shared/components/ui/button/button';

@Component({
  selector: 'app-air-quality-history',
  standalone: true,
  imports: [DatePipe, NgClass, Button],
  templateUrl: './air-quality-history.html',
  styleUrls: ['./air-quality-history.scss']
})
export class AirQualityHistory {
  historyData = input.required<CityHistoryData>();
  startDate = input<string>('');
  endDate = input<string>('');

  // Pagination
  currentPage = signal(1);
  itemsPerPage = 10;

  // Données filtrées par période
  allFilteredData = computed(() => {
    const snapshots = this.historyData().dailySnapshots;
    const start = this.startDate();
    const end = this.endDate();

    const filtered = snapshots.filter(snapshot => {
      const date = new Date(snapshot.date);
      const startDateObj = start ? new Date(start) : null;
      const endDateObj = end ? new Date(end + 'T23:59:59') : null;

      if (startDateObj && date < startDateObj) return false;
      return !(endDateObj && date > endDateObj);
    });

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

  // Nombre de snapshots avec données air quality
  hasAirQualityData = computed(() => {
    return this.allFilteredData().some(s => s.airMeasurement || s.airIndex);
  });

  // Vérifie si on a des données mais aucune air quality
  hasOnlyWeatherData = computed(() => {
    return this.allFilteredData().length > 0 && !this.hasAirQualityData();
  });

  // Fonction pour obtenir la couleur badge selon l'indice qualité
  getQualityBadgeClass(qualityIndex: number | undefined): string {
    if (!qualityIndex) return 'badge-neutral';
    if (qualityIndex <= 2) return 'badge-success';
    if (qualityIndex <= 4) return 'badge-warning';
    return 'badge-error';
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
