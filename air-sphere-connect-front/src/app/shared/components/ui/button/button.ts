import {Component, Input, computed, inject, Output, EventEmitter} from '@angular/core';
import {CommonModule} from '@angular/common';
import {buttonVariants, type ButtonVariants} from '../../../variants/button.variants';
import {type HeroIcon} from '../../../icons/heroicons.registry';
import {IconVariants, iconVariants} from '../../../variants/icon.variants';
import {IconService} from '../../../services/icon';
import {DomSanitizer} from '@angular/platform-browser';


@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './button.html',
  styles: [`
    :host {
      display: inline-block;
    }
  `]
})
export class Button {
  private readonly iconService = inject(IconService);
  private readonly sanitizer = inject(DomSanitizer);
  @Input() color: ButtonVariants['color'] = 'primary';
  @Input() size: ButtonVariants['size'] = 'md';
  @Input() variant: ButtonVariants['variant'] = 'solid';
  @Input() shape: ButtonVariants['shape'] = 'default';
  @Input() padding?: ButtonVariants['padding'];
  @Input() fullWidth: boolean = false;
  @Input() wide?: boolean = false;
  @Input() loading: boolean = false;
  @Input() disabled: boolean = false;
  @Input() type: 'button' | 'submit' | 'reset' | 'checkbox' | 'radio' = 'button';
  @Input() class: string = '';
  @Input() heroIcon?: HeroIcon;
  @Input() icon?: string;
  @Input() iconPosition: 'left' | 'right' = 'left';
  @Input() iconColor?: IconVariants['color'];
  @Input() ariaLabel?: string;
  @Output() buttonClick = new EventEmitter<MouseEvent>();
  @Output() buttonKeydown = new EventEmitter<KeyboardEvent>();

  buttonClasses = computed(() => {
    return buttonVariants({
      color: this.color,
      size: this.size,
      variant: this.variant,
      fullWidth: this.fullWidth,
      shape: this.shape,
      padding: this.padding,
      wide: this.wide,
      class: this.class
    });
  });
  iconHtml = computed(() => {
    if (this.heroIcon) {
      const sizeClasses = iconVariants({
        size: this.size,
        color: 'current'
      });

      const icon = this.iconService.getIconWithClasses(this.heroIcon, sizeClasses);
      return this.sanitizer.bypassSecurityTrustHtml(icon);
    }

    return '';
  });
  iconClasses = computed(() => {
    const colorClass = this.iconColor ? `text-${this.iconColor}` : 'text-current';
    return `flex-shrink-0 ${colorClass}`;
  });

  handleClick(event: MouseEvent): void {
    event.stopPropagation();
    if (!this.disabled && !this.loading) {
      this.buttonClick.emit(event);
    }
  }

  handleKeyDown(event: KeyboardEvent): void {
    // Émettre l'événement keydown pour permettre au parent de le capturer si nécessaire
    this.buttonKeydown.emit(event);

    // Gérer Enter et Space comme des clics (accessibilité)
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      event.stopPropagation();
      if (!this.disabled && !this.loading) {
        const mouseEvent = new MouseEvent('click', {
          bubbles: event.bubbles,
          cancelable: event.cancelable
        });
        this.buttonClick.emit(mouseEvent);
      }
    }
  }

}
