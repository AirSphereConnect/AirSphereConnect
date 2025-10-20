import { Component, ElementRef, Input, ViewChild, signal, effect, DestroyRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { XYContainer, SingleContainer, Line, GroupedBar, Donut, Axis, Tooltip, XYComponentCore } from '@unovis/ts';
import '@unovis/ts/dist/unovis.css';

export type ChartType = 'line' | 'bar' | 'donut';

export interface ChartConfig<T = unknown> {
  type: ChartType;
  data: T[];
  width?: number;
  height?: number;
  color?: string;
  x?: (d: T) => number | string; // Typage plus précis pour x
  y?: (d: T) => number; // Typage plus précis pour y
  value?: (d: T) => number;
  label?: (d: T) => string;
}

@Component({
  selector: 'app-unovis-chart',
  standalone: true,
  imports: [CommonModule],
  template: `<div #container class="unovis-chart" [style.width.px]="width()" [style.height.px]="height()"></div>`,
  styles: [`
    .unovis-chart { min-width: 300px; min-height: 200px; }
    .unovis-chart:empty::before {
      content: 'Loading chart...';
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      color: #666;
    }
    :host ::ng-deep .unovis-tooltip {
      background: white; border: 1px solid #ccc; border-radius: 4px; padding: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
  `]
})
export class UnovisChartComponent {
  @ViewChild('container', { static: true }) containerEl!: ElementRef<HTMLDivElement>;

  @Input() set config(value: ChartConfig) { this.configSignal.set(value); }
  private configSignal = signal<ChartConfig>({ type: 'line', data: [] });
  private container: XYContainer<any> | SingleContainer<any> | null = null;

  protected width = () => this.configSignal().width || 600;
  protected height = () => this.configSignal().height || 300;

  constructor(destroyRef: DestroyRef) {
    effect(() => {
      const config = this.configSignal();
      const el = this.containerEl?.nativeElement;
      if (el?.offsetWidth && el?.offsetHeight && config.data?.length) {
        this.createChart();
      }
    }, { allowSignalWrites: true });

    takeUntilDestroyed(destroyRef);
  }

  ngAfterViewInit(): void {
    const config = this.configSignal();
    const el = this.containerEl?.nativeElement;
    if (el?.offsetWidth && el?.offsetHeight && config.data?.length) {
      this.createChart();
    }
  }

  ngOnDestroy(): void {
    this.destroyChart();
  }

  private createChart(): void {
    this.destroyChart();
    const el = this.containerEl.nativeElement;
    const config = this.configSignal();

    if (!config.data?.length) {
      console.warn('No data provided for chart');
      return;
    }

    switch (config.type) {
      case 'line':
        if (!config.x || !config.y) {
          console.warn('x or y accessor missing for line chart');
          return;
        }
        const line = new Line({ x: config.x, y: config.y, color: config.color });
        this.container = new XYContainer(el, {
          components: [line, new Axis(), new Axis()] as XYComponentCore<any, any>[],
          tooltip: new Tooltip(),
          width: this.width(),
          height: this.height()
        }, config.data);
        break;

      case 'bar':
        if (!config.x || !config.y) {
          console.warn('x or y accessor missing for bar chart');
          return;
        }
        const bar = new GroupedBar({ x: config.x, y: config.y, color: config.color });
        this.container = new XYContainer(el, {
          components: [bar, new Axis(), new Axis()] as XYComponentCore<any, any>[],
          tooltip: new Tooltip(),
          width: this.width(),
          height: this.height()
        }, config.data);
        break;

      case 'donut':
        if (!config.value) {
          console.warn('value accessor missing for donut chart');
          return;
        }
        const donut = new Donut({ value: config.value, color: config.color });
        this.container = new SingleContainer(el, {
          component: donut,
          tooltip: new Tooltip(),
          width: this.width(),
          height: this.height()
        }, config.data);
        break;
    }
  }

  private destroyChart(): void {
    this.container?.destroy();
    this.container = null;
  }
}
