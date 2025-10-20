import { Component, signal } from '@angular/core';
import { UnovisService } from '../../../../../shared/services/unovis';
import { ChartConfig, UnovisChartComponent } from '../../../../../shared/components/unovis-wrapper/unovis-chart';

@Component({
  selector: 'app-chart-test',
  standalone: true,
  imports: [UnovisChartComponent],
  template: `
    <h2>Test Unovis Chart</h2>
    <app-unovis-chart [config]="chartConfig()" />
    <button (click)="updateLine()">Update Line</button>
    <button (click)="updateBar()">Update Bar</button>
    <button (click)="updateDonut()">Update Donut</button>
  `,
})
export class ChartTestComponent {
  chartConfig = signal<ChartConfig<{ x: number | string; y: number } | { value: number; label: string }>>({
    type: 'line',
    data: [
      { x: 1, y: 10 },
      { x: 2, y: 20 },
      { x: 3, y: 15 }
    ],
    width: 600,
    height: 400,
    color: '#7DAF9C'
  });

  constructor(private unovis: UnovisService) {
    this.updateLine(); // initial display
  }

  updateLine() {
    this.chartConfig.set({
      type: 'line',
      data: this.unovis.generateLineData(10),
      x: (d: { x: number; y: number }) => d.x,
      y: (d: { x: number; y: number }) => d.y,
      width: 600,
      height: 400,
      color: this.unovis.theme().primary
    });
  }

  updateBar() {
    const cats = ['Paris', 'Lyon', 'Marseille'];
    this.chartConfig.set({
      type: 'bar',
      data: this.unovis.generateBarData(cats),
      x: (d: { x: string; y: number }) => d.x,
      y: (d: { x: string; y: number }) => d.y,
      width: 600,
      height: 400,
      color: this.unovis.theme().accent
    });
  }

  updateDonut() {
    const labels = ['A', 'B', 'C', 'D'];
    this.chartConfig.set({
      type: 'donut',
      data: this.unovis.generateDonutData(labels),
      value: (d: { value: number; label: string }) => d.value,
      label: (d: { value: number; label: string }) => d.label,
      width: 400,
      height: 400,
      color: this.unovis.theme().colors as any
    });
  }
}
