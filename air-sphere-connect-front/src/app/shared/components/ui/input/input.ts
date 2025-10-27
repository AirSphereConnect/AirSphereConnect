import {
  Component,
  Input,
  computed,
  signal,
  OnInit,
  OnDestroy,
  Output,
  EventEmitter, input,
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { inputVariants, type InputVariants } from '../../../variants/input.variants';
import { NgClass } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { type HeroIconName } from '../../../icons/heroicons.registry';
import {IconComponent} from '../icon/icon';
import { FormErrorService } from '../../../../features/auth/services/form-error.service';

/**
 * Composant principal input
 *
 * @example
 * ```html
 * <app-input
 * label="Nom d'utilisateur"
 * type="text"
 * [control]="usernameControl"
 * [required]="true"
 * iconLeft="user"
 * placeholder="Entrez votre nom d'utilisateur"
 * autocomplete="username"
 * fieldName="username"></app-input>
 * ```
 */
@Component({
  selector: 'app-input',
  templateUrl: './input.html',
  imports: [ReactiveFormsModule, IconComponent, NgClass],
  styleUrl: './input.scss'
})

export class InputComponent implements OnInit, OnDestroy {

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
  @Input() fieldName?: string; // 🆕 Nom du champ pour les messages d'erreur personnalisés

  @Output() iconRightClick = new EventEmitter<void>();

  private _typeSignal = signal<string>('text');

  private formTouched = signal(false);
  private formDirty = signal(false);
  private formValid = signal(false);
  private formDisabled = signal(false);
  private formErrors = signal<any>(null);

  private destroy$ = new Subject<void>();

  inputType = computed(() => this._typeSignal());

  constructor(private formErrorService: FormErrorService) {}

  ngOnInit() {
    if (!this.control) {
      console.error('❌ FormControl non fourni');
      return;
    }

    this.control.statusChanges
      ?.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.updateSignalsFromControl());

    this.control.valueChanges
      ?.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.updateSignalsFromControl());

    this.updateSignalsFromControl();
  }


  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private updateSignalsFromControl() {
    this.formValid.set(this.control.valid);
    this.formDisabled.set(this.control.disabled);
    this.formTouched.set(this.control.touched);
    this.formDirty.set(this.control.dirty);
    this.formErrors.set(this.control.errors);
  }

  shouldShowValidation = computed(() =>
    this.formTouched() || this.formDirty()
  );

  isInvalid = computed(() =>
    this.shouldShowValidation() && !this.formValid()
  );

  isSuccess = computed(() =>
    this.shouldShowValidation() && this.formValid() && !!this.successMessage
  );

  isDisabled = computed(() =>
    this.formDisabled()
  );


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


  // 🔥 Utilisation du service centralisé pour les messages d'erreur
  errorMessage = computed<string | null>(() => {
    if (!this.isInvalid()) return null;
    return this.formErrorService.getErrorMessage(this.formErrors(), this.fieldName);
  });

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
