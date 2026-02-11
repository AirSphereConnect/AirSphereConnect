package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.WeatherResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WeatherMapper Test Suite")
class WeatherMapperTest {

    private WeatherMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mapper = new WeatherMapper(objectMapper);
    }

    @Test
    @DisplayName("toDto should map all fields correctly")
    void toDto_shouldMapAllFields() {
        City city = new City();
        city.setId(1L);
        city.setName("Montpellier");

        LocalDateTime now = LocalDateTime.now();

        WeatherMeasurement weather = new WeatherMeasurement();
        weather.setCity(city);
        weather.setMeasuredAt(now);
        weather.setTemperature(22.5);
        weather.setHumidity(65.0);
        weather.setPressure(1013.0);
        weather.setWindSpeed(3.5);
        weather.setWindDirection(180.0);
        weather.setMessage("[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}]");
        weather.setAlert(false);

        WeatherResponseDto dto = mapper.toDto(weather);

        assertThat(dto).isNotNull();
        assertThat(dto.getCityId()).isEqualTo(1L);
        assertThat(dto.getCityName()).isEqualTo("Montpellier");
        assertThat(dto.getTemperature()).isEqualTo(22.5);
        assertThat(dto.getHumidity()).isEqualTo(65.0);
        assertThat(dto.getPressure()).isEqualTo(1013.0);
        assertThat(dto.getWindSpeed()).isEqualTo(3.5);
        assertThat(dto.getWindDirection()).isEqualTo(180.0);
        assertThat(dto.getAlert()).isFalse();
        assertThat(dto.getMessage()).isNotNull();
    }

    @Test
    @DisplayName("toDto should return null for null weather")
    void toDto_shouldReturnNullForNullWeather() {
        WeatherResponseDto dto = mapper.toDto(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("toDto should handle null message")
    void toDto_shouldHandleNullMessage() {
        City city = new City();
        city.setId(1L);
        city.setName("Paris");

        WeatherMeasurement weather = new WeatherMeasurement();
        weather.setCity(city);
        weather.setMessage(null);
        weather.setAlert(false);

        WeatherResponseDto dto = mapper.toDto(weather);

        assertThat(dto).isNotNull();
        assertThat(dto.getMessage()).isNull();
    }

    @Test
    @DisplayName("toDto should handle invalid JSON message")
    void toDto_shouldHandleInvalidJsonMessage() {
        City city = new City();
        city.setId(1L);
        city.setName("Lyon");

        WeatherMeasurement weather = new WeatherMeasurement();
        weather.setCity(city);
        weather.setMessage("invalid json");
        weather.setAlert(false);

        WeatherResponseDto dto = mapper.toDto(weather);

        assertThat(dto).isNotNull();
        assertThat(dto.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("toDto should handle alert true with valid message")
    void toDto_shouldHandleAlertTrue() {
        City city = new City();
        city.setId(1L);
        city.setName("Marseille");

        WeatherMeasurement weather = new WeatherMeasurement();
        weather.setCity(city);
        weather.setMessage("[{\"sender_name\":\"NWS\",\"event\":\"Storm Warning\"}]");
        weather.setAlert(true);

        WeatherResponseDto dto = mapper.toDto(weather);

        assertThat(dto).isNotNull();
        assertThat(dto.getAlert()).isTrue();
    }
}
