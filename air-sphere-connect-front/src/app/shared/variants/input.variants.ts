import {tv, type VariantProps} from 'tailwind-variants';

export const inputVariants = tv({
  slots: {
    wrapper: 'form-control flex flex-col gap-2',
    label: 'label ml-2',
    labelText: 'label-text font-medium',
    labelRequired: 'text-error ml-1 font-bold',
    inputWrapper: 'relative',
    input: 'input input-bordered w-full transition-all duration-200 placeholder:text-gray-400 placeholder:transition-colors',
    iconLeft: 'absolute left-0 -translate-x-2.5 top-1/2 -translate-y-1/2 pointer-events-none z-10 h-9 w-12 flex items-center justify-center rounded-tl-lg rounded-bl-lg bg-base-300/90',
    iconRight: 'absolute right-3 top-1/2 -translate-y-1/2 z-10',
    helperWrapper: 'label',
    helperText: 'label-text-alt text-base-content/70 text-xs',
    errorText: 'label-text-alt text-error text-xs font-medium flex items-center gap-1.5',
    successText: 'label-text-alt text-success text-xs font-medium flex items-center gap-1.5',
    characterCount: 'label-text-alt text-xs',
  },
  variants: {
    size: {
      xs: {
        input: 'input-xs text-xs placeholder:text-xs',
        labelText: 'text-xs',
        iconLeft: 'left-2',
        iconRight: 'right-2',
        errorText: 'text-xs',
        successText: 'text-xs',
        helperText: 'text-xs',
      },
      sm: {
        input: 'input-sm text-sm placeholder:text-sm',
        labelText: 'text-sm',
        iconLeft: 'left-2.5',
        iconRight: 'right-2.5',
        errorText: 'text-xs',
        successText: 'text-xs',
      },
      md: {
        input: 'input-md text-base placeholder:text-base',
        labelText: 'text-base',
        iconLeft: 'left-3',
        iconRight: 'right-3',
      },
      lg: {
        input: 'input-lg text-lg placeholder:text-lg',
        labelText: 'text-lg',
        iconLeft: 'left-4',
        iconRight: 'right-4',
      }
    },
    state: {
      default: {
        input: 'border-base-300 focus:border-primary focus:outline-primary focus:placeholder:text-base-content/30',
        labelText: 'text-base-content',
      },
      error: {
        input: 'border-2 !border-error focus:!border-error focus:!outline-none focus:ring-2 focus:ring-error/20 !bg-error/5 placeholder:text-error/40 focus:placeholder:text-error/30',
        labelText: 'text-base-content',
      },
      success: {
        input: 'border-2 !border-success focus:!border-success focus:!outline-none focus:ring-2 focus:ring-success/20 placeholder:text-success/40 focus:placeholder:text-success/30',
        labelText: 'text-base-content',
      },
    },
    disabled: {
      true: {
        input: 'cursor-not-allowed opacity-60 bg-base-200 placeholder:text-base-content/20',
        labelText: 'opacity-60',
        wrapper: 'opacity-60',
      },
      false: {}
    },
    fullWidth: {
      true: {
        wrapper: 'w-full'
      },
      false: {
        wrapper: 'w-auto'
      }
    },
    hasIconLeft: {
      true: {
        input: 'pl-10'
      },
      false: {}
    },
    hasIconRight: {
      true: {
        input: 'pr-10'
      },
      false: {}
    }
  },
  compoundVariants: [
    {
      size: 'xs',
      hasIconLeft: true,
      class: { input: 'pl-8' }
    },
    {
      size: 'sm',
      hasIconLeft: true,
      class: { input: 'pl-9' }
    },
    {
      size: 'lg',
      hasIconLeft: true,
      class: { input: 'pl-12' }
    },
    {
      size: 'xs',
      hasIconRight: true,
      class: { input: 'pr-8' }
    },
    {
      size: 'sm',
      hasIconRight: true,
      class: { input: 'pr-9' }
    },
    {
      size: 'lg',
      hasIconRight: true,
      class: { input: 'pr-12' }
    },
  ],
  defaultVariants: {
    size: 'md',
    state: 'default',
    disabled: false,
    fullWidth: true,
    hasIconLeft: false,
    hasIconRight: false,
  }
});

export type InputVariants = VariantProps<typeof inputVariants>;
