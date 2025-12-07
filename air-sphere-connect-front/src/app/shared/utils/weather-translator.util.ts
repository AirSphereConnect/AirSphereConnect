/**
 * Interface pour la structure du message météo JSON
 */
interface WeatherMessageItem {
  main: string;
  description: string;
  icon: string;
}

/**
 * Dictionnaire de traduction des descriptions météo anglais -> français
 */
const WEATHER_TRANSLATIONS: Record<string, string> = {
  // Clear
  'clear sky': 'ciel dégagé',

  // Clouds
  'few clouds': 'quelques nuages',
  'scattered clouds': 'nuages épars',
  'broken clouds': 'nuages fragmentés',
  'overcast clouds': 'ciel couvert',

  // Rain
  'light rain': 'pluie légère',
  'moderate rain': 'pluie modérée',
  'heavy intensity rain': 'forte pluie',
  'very heavy rain': 'très forte pluie',
  'extreme rain': 'pluie extrême',
  'freezing rain': 'pluie verglaçante',
  'light intensity shower rain': 'légères averses',
  'shower rain': 'averses',
  'heavy intensity shower rain': 'fortes averses',
  'ragged shower rain': 'averses irrégulières',

  // Drizzle
  'light intensity drizzle': 'bruine légère',
  'drizzle': 'bruine',
  'heavy intensity drizzle': 'bruine forte',
  'light intensity drizzle rain': 'bruine pluie légère',
  'drizzle rain': 'bruine pluie',
  'heavy intensity drizzle rain': 'bruine pluie forte',
  'shower rain and drizzle': 'averses et bruine',
  'heavy shower rain and drizzle': 'fortes averses et bruine',
  'shower drizzle': 'averses de bruine',

  // Thunderstorm
  'thunderstorm with light rain': 'orage avec pluie légère',
  'thunderstorm with rain': 'orage avec pluie',
  'thunderstorm with heavy rain': 'orage avec forte pluie',
  'light thunderstorm': 'orage léger',
  'thunderstorm': 'orage',
  'heavy thunderstorm': 'orage violent',
  'ragged thunderstorm': 'orage irrégulier',
  'thunderstorm with light drizzle': 'orage avec bruine légère',
  'thunderstorm with drizzle': 'orage avec bruine',
  'thunderstorm with heavy drizzle': 'orage avec bruine forte',

  // Snow
  'light snow': 'neige légère',
  'snow': 'neige',
  'heavy snow': 'neige forte',
  'sleet': 'neige fondue',
  'light shower sleet': 'légères averses de neige fondue',
  'shower sleet': 'averses de neige fondue',
  'light rain and snow': 'pluie et neige légères',
  'rain and snow': 'pluie et neige',
  'light shower snow': 'légères averses de neige',
  'shower snow': 'averses de neige',
  'heavy shower snow': 'fortes averses de neige',

  // Atmosphere
  'mist': 'brume',
  'smoke': 'fumée',
  'haze': 'brume sèche',
  'sand/dust whirls': 'tourbillons de sable/poussière',
  'fog': 'brouillard',
  'sand': 'sable',
  'dust': 'poussière',
  'volcanic ash': 'cendres volcaniques',
  'squalls': 'rafales',
  'tornado': 'tornade',
};

/**
 * Parse et traduit un message météo JSON en français
 * @param message - Le message JSON sous forme de string ou objet déjà parsé
 * @returns La description traduite en français avec majuscule, ou le message original si parsing échoue
 *
 * @example
 * translateWeatherMessage('[{"main":"Clouds","description":"overcast clouds","icon":"04d"}]')
 * // returns "Ciel couvert"
 *
 * translateWeatherMessage([{"main":"Rain","description":"light rain","icon":"10d"}])
 * // returns "Pluie légère"
 */
export function translateWeatherMessage(message: string | WeatherMessageItem[] | undefined): string {
  if (!message) return '-';

  try {
    let weatherArray: WeatherMessageItem[];

    // Si c'est déjà un objet/array, l'utiliser directement
    if (typeof message === 'object') {
      weatherArray = Array.isArray(message) ? message : [message];
    } else {
      // Sinon, c'est une string JSON à parser
      weatherArray = JSON.parse(message);
    }

    if (!Array.isArray(weatherArray)) {
      return typeof message === 'string' ? message : '-';
    }

    if (weatherArray.length === 0) {
      return '-';
    }

    const weather = weatherArray[0];
    const description = String(weather.description || '').trim();

    if (!description) return '';

    const translatedDescription = WEATHER_TRANSLATIONS[description.toLowerCase()] || description;

    // Mettre la première lettre en majuscule
    return translatedDescription.charAt(0).toUpperCase() + translatedDescription.slice(1);
  } catch {
    // Invalid JSON format, return fallback
    return '-';
  }
}
