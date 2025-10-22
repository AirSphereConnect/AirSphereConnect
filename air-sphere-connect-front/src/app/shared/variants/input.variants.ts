import { tv, type VariantProps } from 'tailwind-variants';

export const inputVariants = tv({
  slots: {
    wrapper: 'form-control flex flex-col gap-2 w-full',
    label: 'label ml-2 text-base-content',
    labelText: 'label-text text-base-content',
    labelRequired: 'text-error ml-1',
    inputWrapper: 'relative ',
    input: 'input  input-bordered w-full',
    icon: 'absolute right-3 top-1/2 -translate-y-1/2',
    helperWrapper: 'label',
    helperText: 'label-text-alt text-base-content/70 text-xxs',
    errorText: 'label-text-alt text-error text-xxs',
    successText: 'label-text-alt text-error text-xxs',
  },
  variants: {
    size: {
      xs: {
        input: 'input-xs text-xs',
        labelText: 'text-xs',
        helperText: 'text-xs',
        icon: 'w-4 h-4'
      },
      sm: {
        input: 'input-sm text-sm',
        labelText: 'text-sm',
        helperText: 'text-sm',
        icon: 'w-4 h-4'
      },
      md: {
        input: 'input-md',
        labelText: 'text-base',
        helperText: 'text-md',
        icon: 'w-5 h-5'
      },
      lg: {
        input: 'input-lg text-lg',
        labelText: 'text-lg',
        helperText: 'text-lg',
        icon: 'w-6 h-6'
      }
    },
    state: {
      default: {
        input: 'bg-base-100'
      },
      error: {
        input: 'input-error bg-error ',
        labelText: 'text-error',
        helperText: 'text-error',
        validationIcon: 'text-error'
      },
      success: {
        input: 'input-success',
        labelText: 'text-success',
        helperText: 'text-success',
        validationIcon: 'text-success'
      },
      /*disabled: {
        input: 'input-disabled bg-neutral'
      }*/
    },

    fullWidth: {
      true: {
        wrapper: 'w-full'
      },
      false: {
        wrapper: 'w-auto'
      }
    }
  },
  compoundVariants: [
    /*{
      disabled: true,
      class: 'opacity-60 pointer-events-none'
    }*/
  ],
  defaultVariants: {
    size: 'md',
    state: 'default',
    //disabled: false,
    fullWidth: true
  }
});

export type InputVariants = VariantProps<typeof inputVariants>;
