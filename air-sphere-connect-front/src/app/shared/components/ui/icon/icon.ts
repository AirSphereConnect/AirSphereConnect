import {Component, computed, input} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import {IconService} from '../../../services/icon';
import { type HeroIcon } from '../../../icons/heroicons.registry';
import { iconVariants, type IconVariants } from '../../../variants/icon.variants';

@Component({
  selector: 'app-icon',
  standalone: true,
  template: `
    <span
      [innerHTML]="iconHtml()"
      [class]="iconClasses()">
    </span>`,
})
export class IconComponent {
  name = input.required<HeroIcon>();
  size = input<IconVariants['size']>('md');
  color = input<IconVariants['color']>('current');
  filled = input<boolean | undefined>(undefined);


  constructor(
    private readonly iconService: IconService,
    private readonly sanitizer: DomSanitizer
  ) {}

  iconClasses = computed(() => {
    const baseClasses = iconVariants({
      size: this.size(),
      color: this.color()
    });

    const filledValue = this.filled();

    if (filledValue) {
      return `${baseClasses} [&_svg_path]:fill-current [&_svg_path]:stroke-current transition-all duration-200`;
    } else if (filledValue === false) {
      return `${baseClasses} [&_svg_path]:fill-none transition-all duration-200`;
    }

    return baseClasses;
  });

  iconHtml = computed<SafeHtml>(() => {
    const svg = this.iconService.getIconWithClasses(this.name(), this.iconClasses());
    return this.sanitizer.bypassSecurityTrustHtml(svg);
  });
}
