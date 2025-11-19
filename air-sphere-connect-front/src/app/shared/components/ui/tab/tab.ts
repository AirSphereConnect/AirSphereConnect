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

  getBorderClass(label: string, index: number): string {
    if (this.activeIndex() !== index) {
      return '';
    }
    switch(label.toLowerCase()) {
      case 'mon profil':
        return 'mon-profil';
      case 'mes rubriques':
        return 'mes-rubriques';
      case 'mes favoris':
        return 'mes-favoris';
      case 'mes alertes':
        return 'mes-alertes';
      default:
        return '';
    }
  }

}
