import {Injectable, signal} from '@angular/core';

export interface ChartTheme {
  primary: string;
  accent: string;
  colors: string[];
}

@Injectable({
  providedIn: 'root'
})
export class UnovisService {
  private defaultTheme: ChartTheme = {
    primary: '#7DAF9C',
    accent: '#B8E986',
    colors: ['#7DAF9C', '#B8E986', '#F5F5F0', '#4ADE80', '#F87171', '#FBBF24']
  };

  public theme = signal<ChartTheme>(this.defaultTheme);

  /**
   * Change le thème des graphiques
   */
  setTheme(newTheme: Partial<ChartTheme>): void {
    this.theme.update(current => ({...current, ...newTheme}));
  }

  /**
   * Générateurs de données de test
   */
  generateLineData(points: number = 10): Array<{ x: Date, y: number }> {
    const data = [];
    const now = new Date();

    for (let i = 0; i < points; i++) {
      const date = new Date(now);
      date.setDate(date.getDate() - (points - i));
      data.push({
        x: date,
        y: Math.random() * 50 + 10
      });
    }

    return data;
  }

  generateBarData(categories: string[]): Array<{ x: string, y: number }> {
    return categories.map(cat => ({
      x: cat,
      y: Math.random() * 100 + 10
    }));
  }

  generateDonutData(labels: string[]): Array<{ label: string, value: number }> {
    return labels.map(label => ({
      label,
      value: Math.random() * 100 + 10
    }));
  }

  /**
   * Formatage simple des nombres
   */
  formatNumber(value: number, decimals: number = 0): string {
    return value.toFixed(decimals);
  }

  /**
   * Formatage des dates
   */
  formatDate(date: Date): string {
    return date.toLocaleDateString('fr-FR');
  }

}
