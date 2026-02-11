package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.airSphereConnect.repositories.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WeatherServiceImpl Test Suite")
class WeatherServiceImplTest {

    @Mock
    private WeatherRepository weatherRepository;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    private WeatherMeasurement weather;
    private City city;

    @BeforeEach
    void setUp() {
        city = new City();
        city.setId(1L);
        city.setName("Montpellier");

        weather = new WeatherMeasurement();
        weather.setId(1L);
        weather.setTemperature(22.5);
        weather.setHumidity(65.0);
        weather.setPressure(1013.0);
        weather.setWindSpeed(15.0);
        weather.setCity(city);
        weather.setMeasuredAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("should return latest weather for city")
    void getWeatherByCityId_shouldReturnWeather() {
        when(weatherRepository.findTopByCityIdOrderByMeasuredAtDesc(1L)).thenReturn(Optional.of(weather));

        WeatherMeasurement result = weatherService.getWeatherByCityId(1L);

        assertNotNull(result);
        assertEquals(22.5, result.getTemperature());
        verify(weatherRepository).findTopByCityIdOrderByMeasuredAtDesc(1L);
    }

    @Test
    @DisplayName("should return null when no weather for city")
    void getWeatherByCityId_shouldReturnNull() {
        when(weatherRepository.findTopByCityIdOrderByMeasuredAtDesc(99L)).thenReturn(Optional.empty());

        WeatherMeasurement result = weatherService.getWeatherByCityId(99L);

        assertNull(result);
    }

    @Test
    @DisplayName("should return weather history for city")
    void getWeatherHistoryByCityId_shouldReturnList() {
        when(weatherRepository.findByCityId(1L)).thenReturn(List.of(weather));

        List<WeatherMeasurement> result = weatherService.getWeatherHistoryByCityId(1L);

        assertEquals(1, result.size());
        assertEquals(22.5, result.get(0).getTemperature());
    }

    @Test
    @DisplayName("should return empty list when no history")
    void getWeatherHistoryByCityId_shouldReturnEmptyList() {
        when(weatherRepository.findByCityId(99L)).thenReturn(List.of());

        List<WeatherMeasurement> result = weatherService.getWeatherHistoryByCityId(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return paginated weather data")
    void findAll_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WeatherMeasurement> page = new PageImpl<>(List.of(weather));
        when(weatherRepository.findAll(pageable)).thenReturn(page);

        Page<WeatherMeasurement> result = weatherService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(weatherRepository).findAll(pageable);
    }
}
