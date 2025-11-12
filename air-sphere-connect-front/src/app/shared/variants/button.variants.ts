// src/app/shared/variants/button.variants.ts
import { tv, type VariantProps } from 'tailwind-variants';

export const buttonVariants = tv({
  base: 'btn ',

  variants: {
    color: {
      primary: 'btn-primary',
      secondary: 'btn-secondary',
      accent: 'btn-accent',
      success: 'btn-success',
      warning: 'btn-warning',
      error: 'btn-error',
      info: 'btn-info',
      neutral: 'btn-neutral',
    },

    size: {
      xs: 'btn-xs',
      sm: 'btn-sm',
      md: '',
      lg: 'btn-lg',
      xl: 'btn-xl'
    },

    variant: {
      solid: '',
      outline: 'btn-outline',
      soft: 'btn-soft',
      link: 'btn-link',
      active: 'btn-active',
      ghost: 'btn-ghost'
    },

    fullWidth: {
      true: 'btn-block'
    },

    wide: {
      true: 'btn-wide'
    },

    shape: {
      default: '',
      circle: 'btn-circle',
      square: 'btn-square'
    },

  },

  compoundVariants: [

  ],

  defaultVariants: {
    color: 'primary',
    size: 'md',
    variant: 'solid',
    shape: 'default',
    fullWidth: false,
    loading: false,
    //disabled: false,
    wide: false
  }
});

export type ButtonVariants = VariantProps<typeof buttonVariants>;

