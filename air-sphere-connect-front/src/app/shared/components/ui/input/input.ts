import {
  Component,
  Input,
  computed,
  signal,
  OnInit,
  OnDestroy,
  Output,
  EventEmitter, input, inject, DestroyRef,
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { inputVariants, type InputVariants } from '../../../variants/input.variants';
import { NgClass } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { type HeroIconName } from '../../../icons/heroicons.registry';
import {IconComponent} from '../icon/icon';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-input',
  templateUrl: './input.html',
  imports: [ReactiveFormsModule, IconComponent, NgClass],
  styleUrl: './input.scss'
})

export class InputComponent implements OnInit {

  @Input() label!: string;
  @Input()
  set type(value: string) {
    this._typeSignal.set(value);
  }
  get type(): string {
    return this._typeSignal();
  }
  @Input() allowTypeToggle: boolean = false;
  @Input() control!: FormControl;
  @Input() placeholder: string = '';
  @Input() size: InputVariants['size'] = 'md';
  @Input() fullWidth: boolean = true;
  @Input() helperText?: string;
  @Input() successMessage?: string;
  @Input() required: boolean = false;
  @Input() iconLeft?: HeroIconName;
  @Input() iconRight?: HeroIconName;
  @Input() autocomplete?: string;
  @Input() readonly: boolean = false;

  @Output() iconRightClick = new EventEmitter<void>();

  private _typeSignal = signal<string>('text');

  private formTouched = signal(false);
  private formDirty = signal(false);
  private formValid = signal(false);
  private formDisabled = signal(false);
  private formErrors = signal<any>(null);
  private readonly destroyRef = inject(DestroyRef);

  inputType = computed(() => this._typeSignal());


  ngOnInit() {
    if (!this.control) {
      console.error('❌ FormControl non fourni');
      return;
    }

    this.control.statusChanges
      ?.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.updateSignalsFromControl());

    this.control.valueChanges
      ?.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.updateSignalsFromControl());

    this.updateSignalsFromControl();
  }


  private updateSignalsFromControl() {
    this.formValid.set(this.control.valid);
    this.formDisabled.set(this.control.disabled);
    this.formTouched.set(this.control.touched);
    this.formDirty.set(this.control.dirty);
    this.formErrors.set(this.control.errors);
  }

  shouldShowValidation = computed(() => this.formTouched() || this.formDirty());
  isInvalid = computed(() => this.shouldShowValidation() && !this.formValid());
  isSuccess = computed(() => this.shouldShowValidation() && this.formValid() && !!this.successMessage);
  isDisabled = computed(() => this.formDisabled());


  state = computed<InputVariants['state']>(() => {
    if (!this.shouldShowValidation()) return 'default';
    if (this.isInvalid()) return 'error';
    if (this.isSuccess()) return 'success';
    return 'default';
  });

  stateIconName = computed<HeroIconName | null>(() => {
    if (this.isInvalid()) return 'exclamationTriangle';
    if (this.isSuccess()) return 'check';
    return null;
  });



  private variantsConfig = computed(() => {
    return inputVariants({
      size: this.size,
      state: this.state(),
      fullWidth: this.fullWidth,
      disabled: this.isDisabled(),
      hasIconLeft: !!this.iconLeft,
      hasIconRight: !!this.iconRight || !!this.stateIconName()
    });
  });

  wrapperClass = computed(() => this.variantsConfig().wrapper());
  labelClass = computed(() => this.variantsConfig().label());
  labelTextClass = computed(() => this.variantsConfig().labelText());
  labelRequiredClass = computed(() => this.variantsConfig().labelRequired());
  inputWrapperClass = computed(() => this.variantsConfig().inputWrapper());
  inputClass = computed(() => this.variantsConfig().input());
  iconLeftWrapperClass = computed(() => this.variantsConfig().iconLeft());
  iconRightWrapperClass  = computed(() => this.variantsConfig().iconRight());
  helperWrapperClass = computed(() => this.variantsConfig().helperWrapper());
  helperTextClass = computed(() => this.variantsConfig().helperText());
  errorTextClass = computed(() => this.variantsConfig().errorText());
  successTextClass = computed(() => this.variantsConfig().successText());
  characterCountClass = computed(() => this.variantsConfig().characterCount());


  errorMessage = computed<string | null>(() => {
    if (!this.isInvalid()) return null;

    const errors = this.formErrors();
    if (!errors) return null;

    if (errors['required']) {
      return 'Ce champ est requis';
    }

    if (errors['email']) {
      return 'Veuillez entrer une adresse email valide';
    }

    if (errors['minlength']) {
      const { requiredLength, actualLength } = errors['minlength'];
      const remaining = requiredLength - actualLength;
      return `${remaining} caractère${remaining > 1 ? 's' : ''} manquant${remaining > 1 ? 's' : ''} (minimum ${requiredLength})`;
    }

    if (errors['maxlength']) {
      const { requiredLength, actualLength } = errors['maxlength'];
      const excess = actualLength - requiredLength;
      return `${excess} caractère${excess > 1 ? 's' : ''} en trop (maximum ${requiredLength})`;
    }

    if (errors['pattern']) {
      if (this.type === 'email') {
        return 'Format d\'email invalide';
      }

      return 'Format invalide';
    }

    if (errors['min']) {
      return `La valeur doit être au minimum ${errors['min'].min}`;
    }

    if (errors['max']) {
      return `La valeur ne doit pas dépasser ${errors['max'].max}`;
    }
    return 'Ce champ contient une erreur';
  });

  // 📊 Compteur de caractères
  characterCount = computed(() => {
    const value = this.control.value || '';
    return value.length;
  });

  maxLength = computed(() => {
    const errors = this.formErrors();
    return errors?.['maxlength']?.requiredLength || null;
  });


  showCharacterCount = computed(() => {
    return this.maxLength() !== null;
  });
}
