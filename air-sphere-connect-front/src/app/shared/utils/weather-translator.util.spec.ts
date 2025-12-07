import { translateWeatherMessage } from './weather-translator.util';

describe('translateWeatherMessage', () => {
  describe('Input handling', () => {
    it('should return "-" for undefined input', () => {
      expect(translateWeatherMessage(undefined)).toBe('-');
    });

    it('should return "-" for null input', () => {
      expect(translateWeatherMessage(null as any)).toBe('-');
    });

    it('should return "-" for empty string', () => {
      expect(translateWeatherMessage('')).toBe('-');
    });

    it('should return "-" for invalid JSON string', () => {
      expect(translateWeatherMessage('not a json')).toBe('-');
    });

    it('should return "-" for empty array JSON string', () => {
      expect(translateWeatherMessage('[]')).toBe('-');
    });

    it('should return original string for non-array JSON', () => {
      expect(translateWeatherMessage('{"not": "array"}')).toBe('{"not": "array"}');
    });
  });

  describe('JSON string parsing', () => {
    it('should parse and translate clear sky from JSON string', () => {
      const json = '[{"main":"Clear","description":"clear sky","icon":"01d"}]';
      expect(translateWeatherMessage(json)).toBe('Ciel dégagé');
    });

    it('should parse and translate few clouds from JSON string', () => {
      const json = '[{"main":"Clouds","description":"few clouds","icon":"02d"}]';
      expect(translateWeatherMessage(json)).toBe('Quelques nuages');
    });

    it('should parse and translate overcast clouds from JSON string', () => {
      const json = '[{"main":"Clouds","description":"overcast clouds","icon":"04d"}]';
      expect(translateWeatherMessage(json)).toBe('Ciel couvert');
    });

    it('should parse and translate light rain from JSON string', () => {
      const json = '[{"main":"Rain","description":"light rain","icon":"10d"}]';
      expect(translateWeatherMessage(json)).toBe('Pluie légère');
    });

    it('should parse and translate thunderstorm from JSON string', () => {
      const json = '[{"main":"Thunderstorm","description":"thunderstorm","icon":"11d"}]';
      expect(translateWeatherMessage(json)).toBe('Orage');
    });
  });

  describe('Object/Array input', () => {
    it('should handle object array input directly', () => {
      const weather = [{ main: 'Clear', description: 'clear sky', icon: '01d' }];
      expect(translateWeatherMessage(weather)).toBe('Ciel dégagé');
    });

    it('should handle single object input', () => {
      const weather = { main: 'Rain', description: 'light rain', icon: '10d' } as any;
      expect(translateWeatherMessage(weather)).toBe('Pluie légère');
    });

    it('should use first item from array with multiple elements', () => {
      const weather = [
        { main: 'Rain', description: 'light rain', icon: '10d' },
        { main: 'Clouds', description: 'few clouds', icon: '02d' }
      ];
      expect(translateWeatherMessage(weather)).toBe('Pluie légère');
    });
  });

  describe('Weather translations - Clear', () => {
    it('should translate clear sky', () => {
      expect(translateWeatherMessage('[{"description":"clear sky"}]')).toBe('Ciel dégagé');
    });
  });

  describe('Weather translations - Clouds', () => {
    it('should translate few clouds', () => {
      expect(translateWeatherMessage('[{"description":"few clouds"}]')).toBe('Quelques nuages');
    });

    it('should translate scattered clouds', () => {
      expect(translateWeatherMessage('[{"description":"scattered clouds"}]')).toBe('Nuages épars');
    });

    it('should translate broken clouds', () => {
      expect(translateWeatherMessage('[{"description":"broken clouds"}]')).toBe('Nuages fragmentés');
    });

    it('should translate overcast clouds', () => {
      expect(translateWeatherMessage('[{"description":"overcast clouds"}]')).toBe('Ciel couvert');
    });
  });

  describe('Weather translations - Rain', () => {
    it('should translate light rain', () => {
      expect(translateWeatherMessage('[{"description":"light rain"}]')).toBe('Pluie légère');
    });

    it('should translate moderate rain', () => {
      expect(translateWeatherMessage('[{"description":"moderate rain"}]')).toBe('Pluie modérée');
    });

    it('should translate heavy intensity rain', () => {
      expect(translateWeatherMessage('[{"description":"heavy intensity rain"}]')).toBe('Forte pluie');
    });

    it('should translate shower rain', () => {
      expect(translateWeatherMessage('[{"description":"shower rain"}]')).toBe('Averses');
    });

    it('should translate freezing rain', () => {
      expect(translateWeatherMessage('[{"description":"freezing rain"}]')).toBe('Pluie verglaçante');
    });
  });

  describe('Weather translations - Drizzle', () => {
    it('should translate drizzle', () => {
      expect(translateWeatherMessage('[{"description":"drizzle"}]')).toBe('Bruine');
    });

    it('should translate light intensity drizzle', () => {
      expect(translateWeatherMessage('[{"description":"light intensity drizzle"}]')).toBe('Bruine légère');
    });

    it('should translate heavy intensity drizzle', () => {
      expect(translateWeatherMessage('[{"description":"heavy intensity drizzle"}]')).toBe('Bruine forte');
    });
  });

  describe('Weather translations - Thunderstorm', () => {
    it('should translate thunderstorm', () => {
      expect(translateWeatherMessage('[{"description":"thunderstorm"}]')).toBe('Orage');
    });

    it('should translate thunderstorm with rain', () => {
      expect(translateWeatherMessage('[{"description":"thunderstorm with rain"}]')).toBe('Orage avec pluie');
    });

    it('should translate heavy thunderstorm', () => {
      expect(translateWeatherMessage('[{"description":"heavy thunderstorm"}]')).toBe('Orage violent');
    });

    it('should translate thunderstorm with light drizzle', () => {
      expect(translateWeatherMessage('[{"description":"thunderstorm with light drizzle"}]')).toBe('Orage avec bruine légère');
    });
  });

  describe('Weather translations - Snow', () => {
    it('should translate snow', () => {
      expect(translateWeatherMessage('[{"description":"snow"}]')).toBe('Neige');
    });

    it('should translate light snow', () => {
      expect(translateWeatherMessage('[{"description":"light snow"}]')).toBe('Neige légère');
    });

    it('should translate heavy snow', () => {
      expect(translateWeatherMessage('[{"description":"heavy snow"}]')).toBe('Neige forte');
    });

    it('should translate sleet', () => {
      expect(translateWeatherMessage('[{"description":"sleet"}]')).toBe('Neige fondue');
    });

    it('should translate rain and snow', () => {
      expect(translateWeatherMessage('[{"description":"rain and snow"}]')).toBe('Pluie et neige');
    });
  });

  describe('Weather translations - Atmosphere', () => {
    it('should translate mist', () => {
      expect(translateWeatherMessage('[{"description":"mist"}]')).toBe('Brume');
    });

    it('should translate fog', () => {
      expect(translateWeatherMessage('[{"description":"fog"}]')).toBe('Brouillard');
    });

    it('should translate smoke', () => {
      expect(translateWeatherMessage('[{"description":"smoke"}]')).toBe('Fumée');
    });

    it('should translate haze', () => {
      expect(translateWeatherMessage('[{"description":"haze"}]')).toBe('Brume sèche');
    });

    it('should translate dust', () => {
      expect(translateWeatherMessage('[{"description":"dust"}]')).toBe('Poussière');
    });

    it('should translate tornado', () => {
      expect(translateWeatherMessage('[{"description":"tornado"}]')).toBe('Tornade');
    });

    it('should translate volcanic ash', () => {
      expect(translateWeatherMessage('[{"description":"volcanic ash"}]')).toBe('Cendres volcaniques');
    });
  });

  describe('Unknown descriptions', () => {
    it('should return capitalized original description for unknown weather', () => {
      expect(translateWeatherMessage('[{"description":"unknown weather"}]')).toBe('Unknown weather');
    });

    it('should return capitalized description for unmapped weather', () => {
      expect(translateWeatherMessage('[{"description":"cosmic storm"}]')).toBe('Cosmic storm');
    });
  });

  describe('Case handling', () => {
    it('should handle uppercase description', () => {
      expect(translateWeatherMessage('[{"description":"CLEAR SKY"}]')).toBe('Ciel dégagé');
    });

    it('should handle mixed case description', () => {
      expect(translateWeatherMessage('[{"description":"Clear Sky"}]')).toBe('Ciel dégagé');
    });

    it('should capitalize first letter of translation', () => {
      expect(translateWeatherMessage('[{"description":"light rain"}]')).toBe('Pluie légère');
    });

    it('should capitalize unknown description', () => {
      expect(translateWeatherMessage('[{"description":"new type"}]')).toBe('New type');
    });
  });

  describe('Missing description field', () => {
    it('should handle missing description field', () => {
      expect(translateWeatherMessage('[{"main":"Clear","icon":"01d"}]')).toBe('');
    });

    it('should handle empty description', () => {
      expect(translateWeatherMessage('[{"description":""}]')).toBe('');
    });
  });

  describe('Edge cases', () => {
    it('should handle whitespace in JSON string', () => {
      const json = '  [  { "description" : "clear sky" }  ]  ';
      expect(translateWeatherMessage(json)).toBe('Ciel dégagé');
    });

    it('should handle description with extra spaces', () => {
      expect(translateWeatherMessage('[{"description":"  light rain  "}]')).toBe('Pluie légère');
    });

    it('should handle malformed JSON gracefully', () => {
      expect(translateWeatherMessage('[{"description":"clear sky"')).toBe('-');
    });

    it('should handle numeric description', () => {
      expect(translateWeatherMessage('[{"description":123}]' as any)).toBe('123');
    });
  });
});
