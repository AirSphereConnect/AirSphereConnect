
import {Component, computed, input, output} from '@angular/core';
import {tv} from 'tailwind-variants';

// 🎨 Définition des variantes avec tailwind-variants
const modalVariants = tv({
  slots: {
    container: 'modal-box relative',
    submitButton: 'btn',
    cancelButton: 'btn'
  },
  variants: {
    size: {
      sm: { container: 'max-w-sm' },
      md: { container: 'max-w-md' },
      lg: { container: 'max-w-2xl' },
      xl: { container: 'max-w-5xl' }
    },
    submitVariant: {
      primary: { submitButton: 'btn-primary' },
      secondary: { submitButton: 'btn-secondary' },
      error: { submitButton: 'btn-error' },
      success: { submitButton: 'btn-success' },
      warning: { submitButton: 'btn-warning' },
      neutral: { submitButton: 'btn-neutral' }
    }
  },
  defaultVariants: {
    size: 'md',
    submitVariant: 'primary'
  }
});

/**
 * 🎯 Composant Modal Réutilisable
 *
 * Composant simple pour remplacer les modales custom dans :
 * - thread-list (création de thread)
 * - post (signalement, suppression)
 * - section (création de rubrique)
 *
 * @example
 * ```html
 * <app-modal
 *   [title]="'Titre'"
 *   [show]="showModal()"
 *   (close)="closeModal()"
 *   (submit)="onSubmit()"
 * >
 *   <p>Contenu</p>
 * </app-modal>
 * ```
 */
@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [],
  templateUrl: './modal.component.html'
})
export class ModalComponent {
  // 🔹 Inputs (signaux)
  title = input<string>('');
  show = input<boolean>(false);
  isSubmitting = input<boolean>(false);
  errorMessage = input<string | null>(null);
  submitButtonText = input<string>('Valider');
  cancelButtonText = input<string>('Annuler');
  submitButtonVariant = input<'primary' | 'secondary' | 'error' | 'success' | 'warning' | 'neutral'>('primary');
  showSubmitButton = input<boolean>(true);
  showCancelButton = input<boolean>(true);
  modalSize = input<'sm' | 'md' | 'lg' | 'xl'>('md');

  // 🔹 Outputs (événements)
  close = output<void>();
  submit = output<void>();

  // 🎨 Classes CSS calculées avec tailwind-variants
  readonly classes = computed(() => {
    return modalVariants({
      size: this.modalSize(),
      submitVariant: this.submitButtonVariant()
    });
  });

  onClose() {
    this.close.emit();
  }

  onSubmit() {
    this.submit.emit();
  }

  onBackdropClick(event: MouseEvent) {
    if (event.target === event.currentTarget) {
      this.onClose();
    }
  }
}
