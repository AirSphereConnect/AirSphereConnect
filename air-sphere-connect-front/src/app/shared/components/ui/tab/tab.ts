import {Component, Input, signal, TemplateRef, computed, Output, EventEmitter, effect} from '@angular/core';
import {NgTemplateOutlet} from '@angular/common';
import { tabVariants, type TabVariantProps } from '../../../variants/tab.variants';

export interface TabItem {
  label: string;
  labelTemplate?: TemplateRef<unknown>; // Template optionnel pour le label avec icônes
  template: TemplateRef<unknown>;
  color?: 'primary' | 'secondary' | 'accent'; // Couleur optionnelle pour l'onglet
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
  @Input() variant: TabVariantProps['variant'] = 'default';
  @Input() set activeTabIndex(value: number | undefined) {
    if (value !== undefined && value >= 0) {
      this.activeIndex.set(value);
    }
  }
  @Output() tabChange = new EventEmitter<number>();

  activeIndex = signal(0);

  private readonly variantsConfig = computed(() => tabVariants({ variant: this.variant }));

  navClasses = computed(() => this.variantsConfig().nav());
  panelClasses = computed(() => this.variantsConfig().panel());

  getTabClasses(index: number): string {
    const isActive = this.activeIndex() === index;
    const color = this.tabs[index]?.color || 'primary';
    return tabVariants({ variant: this.variant, active: isActive, color }).tab();
  }

  setActive(index: number): void {
    this.activeIndex.set(index);
    this.tabChange.emit(index);
  }
}
