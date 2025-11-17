import { tv, type VariantProps } from 'tailwind-variants';

export const tabVariants = tv({
  slots: {
    nav: 'tabs flex ',
    tab: 'tab flex-1  text-center cursor-pointer font-semibold transition-all duration-200',
    panel: 'pt-6',
  },

  variants: {
    variant: {
      default: {
        tab: '',
      },
      bordered: {
        tab: 'border-b-3 h-16',
      },
    },

    active: {
      true: {},
      false: {},
    },

    color: {
      primary: {},
      secondary: {},
      accent: {},
    },
  },

  compoundVariants: [
    {
      variant: 'default',
      active: true,
      class: {
        tab: 'tab-active',
      },
    },

    // Bordered variant - active - primary
    {
      variant: 'bordered',
      active: true,
      color: 'primary',
      class: {
        tab: 'text-primary border-primary bg-base-100',
      },
    },

    // Bordered variant - active - secondary
    {
      variant: 'bordered',
      active: true,
      color: 'secondary',
      class: {
        tab: 'text-secondary border-secondary bg-base-100',
      },
    },

    // Bordered variant - active - accent
    {
      variant: 'bordered',
      active: true,
      color: 'accent',
      class: {
        tab: 'text-accent border-accent bg-base-100',
      },
    },

    // Bordered variant - inactive
    {
      variant: 'bordered',
      active: false,
      class: {
        tab: 'text-base-content opacity-60 border-transparent hover:bg-base-200',
      },
    },
  ],

  defaultVariants: {
    variant: 'default',
    active: false,
    color: 'primary',
  },
});

export type TabVariantProps = VariantProps<typeof tabVariants>;
