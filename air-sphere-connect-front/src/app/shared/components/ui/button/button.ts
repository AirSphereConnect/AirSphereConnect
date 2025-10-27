import {Component, Input, computed, inject, Output, EventEmitter, input, ChangeDetectorRef} from '@angular/core';
import {CommonModule} from '@angular/common';
import {buttonVariants, type ButtonVariants} from '../../../variants/button.variants';
import {type HeroIconName} from '../../../icons/heroicons.registry';
import {IconVariants, iconVariants} from '../../../variants/icon.variants';
import {IconService} from '../../../services/icon';
import {DomSanitizer} from '@angular/platform-browser';

/**
 * # Composant Button - Documentation complète
 *
 * Composant bouton personnalisable basé sur DaisyUI et Tailwind CSS.
 * Supporte de multiples variantes, tailles, couleurs et états (loading, disabled).
 *
 * ## 🎨 Variantes disponibles
 *
 * ### Couleurs (color)
 * - `primary` - Bouton principal (défaut)
 * - `secondary` - Bouton secondaire
 * - `accent` - Bouton d'accentuation
 * - `success` - Action de succès
 * - `warning` - Avertissement
 * - `error` - Action destructive
 * - `info` - Information
 * - `neutral` - Neutre
 *
 * ### Tailles (size)
 * - `xs` - Extra petit
 * - `sm` - Petit
 * - `md` - Moyen (défaut)
 * - `lg` - Grand
 * - `xl` - Extra grand
 *
 * ### Styles (variant)
 * - `solid` - Plein (défaut)
 * - `outline` - Contour uniquement
 * - `soft` - Doux
 * - `link` - Style lien
 * - `active` - État actif
 * - `ghost` - Transparent
 *
 * ### Formes (shape)
 * - `default` - Rectangle arrondi (défaut)
 * - `circle` - Circulaire (pour icônes seules)
 * - `square` - Carré
 *
 * ## 📋 Exemples d'utilisation
 *
 * ### Bouton basique
 * ```html
 * <app-button>Cliquer ici</app-button>
 * ```
 *
 * ### Bouton avec couleur et taille
 * ```html
 * <app-button color="primary" size="lg">Bouton principal</app-button>
 * <app-button color="secondary" size="sm">Bouton secondaire</app-button>
 * <app-button color="error" size="md">Supprimer</app-button>
 * ```
 *
 * ### Boutons avec variantes
 * ```html
 * <app-button variant="outline" color="primary">Outline</app-button>
 * <app-button variant="ghost" color="accent">Ghost</app-button>
 * <app-button variant="link">Lien</app-button>
 * ```
 *
 * ### Boutons avec icônes (HeroIcons)
 * ```html
 * <!-- Icône à gauche (défaut) -->
 * <app-button heroIcon="plus" color="success">Ajouter</app-button>
 *
 * <!-- Icône à droite -->
 * <app-button heroIcon="arrowRight" iconPosition="right">Suivant</app-button>
 *
 * <!-- Icône seule avec forme circulaire -->
 * <app-button heroIcon="xMark" shape="circle" color="error" variant="ghost"></app-button>
 *
 * <!-- Icône seule avec forme carrée -->
 * <app-button heroIcon="pencil" shape="square" size="sm"></app-button>
 * ```
 *
 * ### Boutons avec états
 * ```html
 * <!-- État de chargement -->
 * <app-button [loading]="isLoading" color="primary">
 *   Enregistrer
 * </app-button>
 *
 * <!-- État désactivé -->
 * <app-button [disabled]="true" color="secondary">
 *   Désactivé
 * </app-button>
 * ```
 *
 * ### Boutons de formulaire
 * ```html
 * <!-- Bouton de soumission -->
 * <app-button type="submit" color="primary" [disabled]="form.invalid">
 *   Valider
 * </app-button>
 *
 * <!-- Bouton de réinitialisation -->
 * <app-button type="reset" variant="outline" color="neutral">
 *   Réinitialiser
 * </app-button>
 * ```
 *
 * ### Largeur pleine
 * ```html
 * <app-button [fullWidth]="true" color="primary">
 *   Bouton pleine largeur
 * </app-button>
 * ```
 *
 * ### Bouton large (wide)
 * ```html
 * <app-button [wide]="true" color="accent">
 *   Bouton élargi
 * </app-button>
 * ```
 *
 * ### Gestion des événements
 * ```html
 * <app-button (click)="handleClick()" color="primary">
 *   Cliquer
 * </app-button>
 * ```
 *
 * ```typescript
 * // Dans le composant
 * handleClick(): void {
 *   console.log('Bouton cliqué !');
 * }
 * ```
 *
 * ### Exemples de cas d'usage réels
 * ```html
 * <!-- Barre d'actions CRUD -->
 * <div class="flex gap-2">
 *   <app-button heroIcon="plus" color="success" size="sm">
 *     Ajouter
 *   </app-button>
 *   <app-button heroIcon="pencil" color="primary" size="sm" variant="outline">
 *     Modifier
 *   </app-button>
 *   <app-button heroIcon="trash" color="error" size="sm" variant="ghost">
 *     Supprimer
 *   </app-button>
 * </div>
 *
 * <!-- Dialogue de confirmation -->
 * <div class="flex gap-3 justify-end">
 *   <app-button variant="outline" color="neutral" (click)="cancel()">
 *     Annuler
 *   </app-button>
 *   <app-button color="primary" [loading]="isSaving" (click)="save()">
 *     Confirmer
 *   </app-button>
 * </div>
 *
 * <!-- Pagination -->
 * <div class="flex gap-1">
 *   <app-button heroIcon="chevronLeft" shape="square" size="sm" variant="outline">
 *   </app-button>
 *   <app-button variant="ghost" size="sm">1</app-button>
 *   <app-button variant="solid" size="sm">2</app-button>
 *   <app-button variant="ghost" size="sm">3</app-button>
 *   <app-button heroIcon="chevronRight" shape="square" size="sm" variant="outline">
 *   </app-button>
 * </div>
 * ```
 *
 * ## 🎯 Combinaisons recommandées
 *
 * ### Actions primaires
 * ```html
 * <app-button color="primary" size="lg">Action principale</app-button>
 * ```
 *
 * ### Actions destructives
 * ```html
 * <app-button color="error" variant="outline" heroIcon="trash">
 *   Supprimer
 * </app-button>
 * ```
 *
 * ### Boutons d'icône uniquement
 * ```html
 * <app-button shape="circle" variant="ghost" heroIcon="xMark" ariaLabel="Fermer">
 * </app-button>
 * ```
 *
 * ## ♿ Accessibilité
 *
 * - Utilisez `ariaLabel` pour les boutons avec icône uniquement
 * - Le type `button` empêche la soumission accidentelle de formulaires
 * - Les états `disabled` et `loading` sont gérés automatiquement
 * - Le bouton émet `stopPropagation` pour éviter les clics multiples
 *
 * ```html
 * <app-button
 *   heroIcon="xMark"
 *   shape="circle"
 *   ariaLabel="Fermer la fenêtre"
 *   variant="ghost">
 * </app-button>
 * ```
 *
 * ## 🔧 Propriétés
 *
 * | Propriété | Type | Défaut | Description |
 * |-----------|------|--------|-------------|
 * | color | ButtonVariants['color'] | 'primary' | Couleur du bouton |
 * | size | ButtonVariants['size'] | 'md' | Taille du bouton |
 * | variant | ButtonVariants['variant'] | 'solid' | Style du bouton |
 * | shape | ButtonVariants['shape'] | 'default' | Forme du bouton |
 * | fullWidth | boolean | false | Bouton pleine largeur |
 * | wide | boolean | false | Bouton élargi |
 * | loading | boolean | false | État de chargement |
 * | disabled | boolean | false | État désactivé |
 * | type | 'button' \| 'submit' \| 'reset' | 'button' | Type HTML du bouton |
 * | heroIcon | HeroIconName | undefined | Nom de l'icône HeroIcon |
 * | iconPosition | 'left' \| 'right' | 'left' | Position de l'icône |
 * | iconColor | IconVariants['color'] | undefined | Couleur de l'icône |
 * | ariaLabel | string | undefined | Label d'accessibilité |
 * | class | string | '' | Classes CSS personnalisées |
 *
 * ## 📤 Événements
 *
 * | Événement | Type | Description |
 * |-----------|------|-------------|
 * | click | EventEmitter<MouseEvent> | Émis lors du clic (si non désactivé/loading) |
 *
 * ## 📸 Captures d'écran
 *
 * Pour voir les rendus visuels, consultez :
 * - `src/assets/docs/button-colors.png` - Toutes les couleurs
 * - `src/assets/docs/button-sizes.png` - Toutes les tailles
 * - `src/assets/docs/button-variants.png` - Toutes les variantes
 * - `src/assets/docs/button-shapes.png` - Toutes les formes
 *
 * ## 📚 Voir aussi
 *
 * - {@link buttonVariants} - Configuration des variantes Tailwind
 * - {@link IconComponent} - Composant d'icônes
 *
 * @usageNotes
 *
 * ### Bonnes pratiques
 * 1. Utilisez `type="button"` par défaut pour éviter la soumission de formulaires
 * 2. Préférez `heroIcon` pour les icônes (HeroIcons intégrées)
 * 3. Toujours ajouter `ariaLabel` pour les boutons avec icône seule
 * 4. Utilisez `loading` pour les actions asynchrones
 * 5. N'oubliez pas `stopPropagation` pour éviter les clics multiples (géré automatiquement)
 *
 * ### À éviter
 * - ❌ Ne pas utiliser `disabled` ET `loading` simultanément (loading désactive déjà)
 * - ❌ Ne pas oublier le contenu textuel ou `ariaLabel` (accessibilité)
 * - ❌ Ne pas abuser des couleurs vives (error, warning) pour des actions courantes
 */
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
      this.click.emit(event);
    }
  }

}
