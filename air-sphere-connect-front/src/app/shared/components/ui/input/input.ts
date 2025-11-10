import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  forwardRef,
  inject,
  DestroyRef,
  computed,
  signal,
} from '@angular/core';
import {
  ControlValueAccessor,
  FormControl,
  NG_VALUE_ACCESSOR,
  ReactiveFormsModule,
} from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormErrorService } from '../../../../features/auth/services/form-error.service';
import { inputVariants, type InputVariants } from '../../../variants/input.variants';
import { NgClass } from '@angular/common';
import { type HeroIconName } from '../../../icons/heroicons.registry';
import { IconComponent } from '../icon/icon';

@Component({
  selector: 'app-input',
  templateUrl: './input.html',
  styleUrls: ['./input.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputComponent),
      multi: true,
    },
  ],
  imports: [ReactiveFormsModule, IconComponent, NgClass],
})
export class InputComponent implements ControlValueAccessor, OnInit {
  @Input() label!: string;
  @Input()
  set type(value: string) {
    this._typeSignal.set(value);
  }
  get type(): string {
    return this._typeSignal();
  }
  @Input() allowTypeToggle: boolean = false;

  // Rend le control optionnel pour ne pas casser l'utilisation avec formControlName
  @Input() control?: FormControl;

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
  @Input() fieldName?: string;

  @Output() iconRightClick = new EventEmitter<void>();

  private _typeSignal = signal<string>('text');

  private formTouched = signal(false);
  private formDirty = signal(false);
  private formValid = signal(false);
  private formDisabled = signal(false);
  private formErrors = signal<any>(null);
  private readonly destroyRef = inject(DestroyRef);
  private readonly formErrorService = inject(FormErrorService);

  inputType = computed(() => this._typeSignal());

  // ControlValueAccessor internal state
  value: string = '';
  disabled = false;

  private onChange: (value: any) => void = () => {};
  private onTouched: () => void = () => {};

  // ControlValueAccessor implementations
  writeValue(value: any): void {
    this.value = value ?? '';
    if (this.control && this.control.value !== value) {
      this.control.setValue(value, { emitEvent: false });
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  onInput(event: Event): void {
    console.log("valeur html input element : " + (event.target as HTMLInputElement))
    const inputValue = (event.target as HTMLInputElement).value;
    this.value = inputValue;
    this.onChange(inputValue);
    if (this.control && this.control.value !== inputValue) {
      this.control.setValue(inputValue, { emitEvent: false });
    }
  }

  onBlur(): void {
    this.onTouched();
  }

  ngOnInit(): void {
    if (this.control) {
      this.control.statusChanges
        ?.pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => this.updateSignalsFromControl());

      this.control.valueChanges
        ?.pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => {
          this.updateSignalsFromControl();
          // Sync control value to internal value if it changed externally
          if (this.control && this.control.value !== this.value) {
            this.value = this.control.value ?? '';
          }
        });

      this.updateSignalsFromControl();
    }
  }

  private updateSignalsFromControl(): void {
    if (!this.control) return;
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
      hasIconRight: !!this.iconRight || !!this.stateIconName(),
    });
  });

  wrapperClass = computed(() => this.variantsConfig().wrapper());
  labelClass = computed(() => this.variantsConfig().label());
  labelTextClass = computed(() => this.variantsConfig().labelText());
  labelRequiredClass = computed(() => this.variantsConfig().labelRequired());
  inputWrapperClass = computed(() => this.variantsConfig().inputWrapper());
  inputClass = computed(() => this.variantsConfig().input());
  iconLeftWrapperClass = computed(() => this.variantsConfig().iconLeft());
  iconRightWrapperClass = computed(() => this.variantsConfig().iconRight());
  helperWrapperClass = computed(() => this.variantsConfig().helperWrapper());
  helperTextClass = computed(() => this.variantsConfig().helperText());
  errorTextClass = computed(() => this.variantsConfig().errorText());
  successTextClass = computed(() => this.variantsConfig().successText());
  characterCountClass = computed(() => this.variantsConfig().characterCount());

  errorMessage = computed<string | null>(() => {
    if (!this.isInvalid()) return null;
    return this.formErrorService.getErrorMessage(this.formErrors(), this.fieldName);
  });

  characterCount = computed(() => {
    const val = this.control?.value ?? this.value;
    return val.length;
  });

  maxLength = computed(() => {
    const errors = this.formErrors();
    return errors?.['maxlength']?.requiredLength ?? null;
  });

  showCharacterCount = computed(() => this.maxLength() !== null);
}
