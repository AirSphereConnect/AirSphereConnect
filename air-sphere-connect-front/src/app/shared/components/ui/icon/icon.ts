import { Component, Input, computed } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import {IconService} from '../../../services/icon';
import { type HeroIconName } from '../../../icons/heroicons.registry';
import { iconVariants, type IconVariants } from '../../../variants/icon.variants';

@Component({
  selector: 'app-icon',
  standalone: true,
  template: `<span [innerHTML]="iconHtml()" [class]="iconClasses()"></span>`,
})
export class IconComponent {
  @Input({ required: true }) name!: HeroIconName;
  @Input() size: IconVariants['size'] = 'md';
  @Input() color: IconVariants['color'] = 'current';

  constructor(
    private iconService: IconService,
    private sanitizer: DomSanitizer
  ) {}

  iconClasses = computed(() => {
    return iconVariants({
      size: this.size,
      color: this.color
    });
  });

  iconHtml = computed<SafeHtml>(() => {
    const svg = this.iconService.getIconWithClasses(this.name, this.iconClasses());
    return this.sanitizer.bypassSecurityTrustHtml(svg);
  });
}
