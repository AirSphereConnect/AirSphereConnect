import {Component, Input, signal, TemplateRef} from '@angular/core';
import {NgTemplateOutlet} from '@angular/common';

export interface TabItem {
  label: string;
  template: TemplateRef<unknown>;
}


@Component({
  selector: 'app-tab',
  templateUrl: './tab.html',
  styleUrls: ['./tab.scss'],
  imports: [
    NgTemplateOutlet
  ],
  standalone: true
})
export class Tab {
  @Input() tabs: TabItem[] = [];
  @Input() navClass = '';

  activeIndex = signal(0);

  setActive(index: number): void {
    this.activeIndex.set(index);
  }
}
