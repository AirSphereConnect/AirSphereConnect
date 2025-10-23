import {Component, Input, computed, inject, Output, EventEmitter, input, ChangeDetectorRef} from '@angular/core';
import {CommonModule} from '@angular/common';
import {buttonVariants, type ButtonVariants} from '../../../variants/button.variants';
import {type HeroIconName} from '../../../icons/heroicons.registry';
import {IconVariants, iconVariants} from '../../../variants/icon.variants';
import {IconService} from '../../../services/icon';
import {DomSanitizer} from '@angular/platform-browser';


@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button
      [ngClass]="buttonClasses()"
      [disabled]="disabled || loading"
      [type]="type"
      [attr.aria-label]="ariaLabel"
      (click)="handleClick($event)"
    >
      @if (loading) {
        <span class="loading loading-spinner"></span>
      }

      @if ((heroIcon || icon) && iconPosition === 'left' && !loading) {
        <span
          [innerHTML]="iconHtml()"
          [ngClass]="iconClasses()">
        </span>
      }

      <ng-content></ng-content>

      @if ((heroIcon || icon) && iconPosition === 'right' && !loading) {
        <span
          [innerHTML]="iconHtml()"
          [ngClass]="iconClasses()">
        </span>
      }
    </button>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
  `]
})
export class Button {
  private iconService = inject(IconService);
  private sanitizer = inject(DomSanitizer);

  @Input() color: ButtonVariants['color'] = 'primary';
  @Input() size: ButtonVariants['size'] = 'md';
  @Input() variant: ButtonVariants['variant'] = 'solid';
  @Input() shape: ButtonVariants['shape'] = 'default';
  @Input() fullWidth: boolean = false;
  @Input() wide?: boolean = false;
  @Input() loading: boolean = false;
  @Input() disabled: boolean = false;
  @Input() type: 'button' | 'submit' | 'reset' | 'checkbox' | 'radio' = 'button';
  @Input() class: string = '';

  @Input() heroIcon?: HeroIconName;
  @Input() icon?: string;
  @Input() iconPosition: 'left' | 'right' = 'left';
  @Input() iconColor?: IconVariants['color'];

  @Input() ariaLabel?: string;
  @Output() click = new EventEmitter<MouseEvent>();


  buttonClasses = computed(() => {
    return buttonVariants({
      color: this.color,
      size: this.size,
      variant: this.variant,
      fullWidth: this.fullWidth,
      shape: this.shape,
      wide: this.wide,
      //isDisabled: this.disabled || this.loading,
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

    if (!this.disabled && !this.loading) {
      this.click.emit(event);
    }

    console.log(event)
  }

}
