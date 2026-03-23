package com.airsphereconnect.services.api;

import com.airsphereconnect.dtos.response.ApiWeatherResponseDto;
import com.airsphereconnect.dtos.response.WeatherAlertDto;
import com.airsphereconnect.dtos.response.WeatherDescriptionDto;
import com.airsphereconnect.dtos.response.WeatherMainDto;
import com.airsphereconnect.dtos.response.WeatherWindDto;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.WeatherMeasurement;
import com.airsphereconnect.repositories.CityRepository;
import com.airsphereconnect.repositories.WeatherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WeatherSyncService Test Suite")
class WeatherSyncServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private WeatherSyncService weatherSyncService;

    @BeforeEach
    void setUp() {
        weatherSyncService = new WeatherSyncService(
                cityRepository,
                weatherRepository,
                objectMapper,
                webClient
        );
        ReflectionTestUtils.setField(weatherSyncService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(weatherSyncService, "minPopulation", 5000);
    }

    @Nested
    @DisplayName("Interface methods tests")
    class InterfaceMethodsTests {

        @Test
        @DisplayName("getServiceName should return WEATHER")
        void getServiceName_shouldReturnWeather() {
            assertThat(weatherSyncService.getServiceName()).isEqualTo("WEATHER");
        }

        @Test
        @DisplayName("isEnabled should return true")
        void isEnabled_shouldReturnTrue() {
            assertThat(weatherSyncService.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("getSyncInterval should return 1 hour")
        void getSyncInterval_shouldReturnOneHour() {
            assertThat(weatherSyncService.getSyncInterval()).isEqualTo(Duration.ofHours(1));
        }

        @Test
        @DisplayName("getLastSync should return null initially")
        void getLastSync_shouldReturnNullInitially() {
            assertThat(weatherSyncService.getLastSync()).isNull();
        }

        @Test
        @DisplayName("getConsecutiveErrors should return 0 initially")
        void getConsecutiveErrors_shouldReturnZeroInitially() {
            assertThat(weatherSyncService.getConsecutiveErrors()).isZero();
        }
    }

    @Nested
    @DisplayName("syncData tests")
    class SyncDataTests {

        @Test
        @DisplayName("should update lastSync on success")
        void syncData_shouldUpdateLastSyncOnSuccess() {
            City city = createCity();

            WeatherMainDto mainDto = new WeatherMainDto(25.0, 60.0, 1013.0);
            WeatherWindDto windDto = new WeatherWindDto(5.0, 180.0);
            ApiWeatherResponseDto response = new ApiWeatherResponseDto(mainDto, windDto, null, null);

            when(cityRepository.findDistinctByPopulations_CountGreaterThanEqual(anyInt())).thenReturn(List.of(city));
            setupWebClientMock(response);
            when(weatherRepository.existsByCityAndMeasuredAtBetween(any(), any(), any())).thenReturn(false);
            when(weatherRepository.saveAll(any())).thenReturn(List.of());

            weatherSyncService.syncData();

            assertThat(weatherSyncService.getLastSync()).isNotNull();
            assertThat(weatherSyncService.getConsecutiveErrors()).isZero();
        }

        @Test
        @DisplayName("should increment errors on failure")
        void syncData_shouldIncrementErrorsOnFailure() {
            when(cityRepository.findDistinctByPopulations_CountGreaterThanEqual(anyInt())).thenThrow(new RuntimeException("Database error"));

            weatherSyncService.syncData();

            assertThat(weatherSyncService.getConsecutiveErrors()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("fetchAndStoreWeatherForAllCities tests")
    class FetchAndStoreTests {

        @Test
        @DisplayName("should fetch and store weather for all cities")
        void shouldFetchAndStoreWeatherForAllCities() {
            City city = createCity();

            WeatherMainDto mainDto = new WeatherMainDto(25.5, 60.0, 1013.0);
            WeatherWindDto windDto = new WeatherWindDto(5.0, 180.0);
            ApiWeatherResponseDto response = new ApiWeatherResponseDto(mainDto, windDto, null, null);

            when(cityRepository.findDistinctByPopulations_CountGreaterThanEqual(anyInt())).thenReturn(List.of(city));
            setupWebClientMock(response);
            when(weatherRepository.existsByCityAndMeasuredAtBetween(any(), any(), any())).thenReturn(false);
            when(weatherRepository.saveAll(any())).thenReturn(List.of());

            weatherSyncService.fetchAndStoreWeatherForAllCities();

            verify(weatherRepository).saveAll(any());
        }

        @Test
        @DisplayName("should skip duplicate measurements")
        void shouldSkipDuplicateMeasurements() {
            City city = createCity();

            WeatherMainDto mainDto = new WeatherMainDto(25.5, 60.0, 1013.0);
            ApiWeatherResponseDto response = new ApiWeatherResponseDto(mainDto, null, null, null);

            when(cityRepository.findDistinctByPopulations_CountGreaterThanEqual(anyInt())).thenReturn(List.of(city));
            setupWebClientMock(response);
            when(weatherRepository.existsByCityAndMeasuredAtBetween(any(), any(), any())).thenReturn(true);

            weatherSyncService.fetchAndStoreWeatherForAllCities();

            verify(weatherRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("should handle empty city list")
        void shouldHandleEmptyCityList() {
            when(cityRepository.findDistinctByPopulations_CountGreaterThanEqual(anyInt())).thenReturn(List.of());

            weatherSyncService.fetchAndStoreWeatherForAllCities();

            verify(weatherRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("should convert wind speed from m/s to km/h")
        void shouldConvertWindSpeed() {
            City city = createCity();

            WeatherMainDto mainDto = new WeatherMainDto(25.0, 60.0, 1013.0);
            WeatherWindDto windDto = new WeatherWindDto(10.0, 180.0); // 10 m/s = 36 km/h
            ApiWeatherResponseDto response = new ApiWeatherResponseDto(mainDto, windDto, null, null);

            when(cityRepository.findDistinctByPopulations_CountGreaterThanEqual(anyInt())).thenReturn(List.of(city));
            setupWebClientMock(response);
            when(weatherRepository.existsByCityAndMeasuredAtBetween(any(), any(), any())).thenReturn(false);

            ArgumentCaptor<List<WeatherMeasurement>> captor = ArgumentCaptor.forClass(List.class);
            when(weatherRepository.saveAll(captor.capture())).thenReturn(List.of());

            weatherSyncService.fetchAndStoreWeatherForAllCities();

            List<WeatherMeasurement> saved = captor.getValue();
            assertThat(saved).hasSize(1);
            assertThat(saved.get(0).getWindSpeed()).isEqualTo(36.0);
        }

        @Test
        @DisplayName("should handle weather with alerts")
        void shouldHandleWeatherWithAlerts() throws Exception {
            City city = createCity();

            WeatherMainDto mainDto = new WeatherMainDto(25.0, 60.0, 1013.0);
            List<WeatherAlertDto> alerts = List.of(new WeatherAlertDto("NWS", "Storm", "description"));
            ApiWeatherResponseDto response = new ApiWeatherResponseDto(mainDto, null, null, alerts);

            when(cityRepository.findDistinctByPopulations_CountGreaterThanEqual(anyInt())).thenReturn(List.of(city));
            setupWebClientMock(response);
            when(weatherRepository.existsByCityAndMeasuredAtBetween(any(), any(), any())).thenReturn(false);
            when(objectMapper.writeValueAsString(any())).thenReturn("[{\"event\":\"Storm\"}]");

            ArgumentCaptor<List<WeatherMeasurement>> captor = ArgumentCaptor.forClass(List.class);
            when(weatherRepository.saveAll(captor.capture())).thenReturn(List.of());

            weatherSyncService.fetchAndStoreWeatherForAllCities();

            List<WeatherMeasurement> saved = captor.getValue();
            assertThat(saved).hasSize(1);
            assertThat(saved.get(0).getAlert()).isTrue();
        }

        @Test
        @DisplayName("should handle weather description")
        void shouldHandleWeatherDescription() throws Exception {
            City city = createCity();

            WeatherMainDto mainDto = new WeatherMainDto(25.0, 60.0, 1013.0);
            List<WeatherDescriptionDto> descriptions = List.of(new WeatherDescriptionDto("Clear", "clear sky", "01d"));
            ApiWeatherResponseDto response = new ApiWeatherResponseDto(mainDto, null, descriptions, null);

            when(cityRepository.findDistinctByPopulations_CountGreaterThanEqual(anyInt())).thenReturn(List.of(city));
            setupWebClientMock(response);
            when(weatherRepository.existsByCityAndMeasuredAtBetween(any(), any(), any())).thenReturn(false);
            when(objectMapper.writeValueAsString(any())).thenReturn("[{\"description\":\"clear sky\"}]");

            ArgumentCaptor<List<WeatherMeasurement>> captor = ArgumentCaptor.forClass(List.class);
            when(weatherRepository.saveAll(captor.capture())).thenReturn(List.of());

            weatherSyncService.fetchAndStoreWeatherForAllCities();

            List<WeatherMeasurement> saved = captor.getValue();
            assertThat(saved).hasSize(1);
            assertThat(saved.get(0).getMessage()).isNotNull();
        }
    }

    // Helper methods
    private City createCity() {
        City city = new City();
        city.setId(1L);
        city.setName("Montpellier");
        city.setLatitude(43.6119);
        city.setLongitude(3.8772);
        return city;
    }

    @SuppressWarnings("unchecked")
    private void setupWebClientMock(ApiWeatherResponseDto response) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ApiWeatherResponseDto.class)).thenReturn(Mono.just(response));
    }
}
