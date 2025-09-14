/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}"
  ],
  theme: {
    extend: {
      colors: {
        // Charte graphique AirSphere (simplifié)
        'primary': '#7DAF9C',
        'accent': '#B8E986',
        'neutral': '#F5F5F0'
      }
    }
  },
  plugins: []
}
