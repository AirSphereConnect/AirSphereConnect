import { tv, type VariantProps } from 'tailwind-variants';

export const iconVariants = tv({
  base: 'flex-shrink-0',

  variants: {
    size: {
      xs: 'w-3 h-3',
      sm: 'w-4 h-4',
      md: 'w-5 h-5',
      lg: 'w-6 h-6',
      xl: 'w-8 h-8'
    },

    color: {
      current: 'text-current',
      primary: 'text-primary',
      secondary: 'text-secondary',
      accent: 'text-accent',
      success: 'text-success',
      warning: 'text-warning',
      error: 'text-error',
      info: 'text-info',
      neutral: 'text-neutral',

    }

  },

  defaultVariants: {
    color: 'current',
    size: 'md',
    position: 'left'
  }
});

export type IconVariants = VariantProps<typeof iconVariants>;
