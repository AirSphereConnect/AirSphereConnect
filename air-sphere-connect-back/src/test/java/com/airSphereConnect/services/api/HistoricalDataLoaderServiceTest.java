package com.airSphereConnect.services.api;

import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.repositories.AirQualityMeasurementRepository;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistoricalDataLoaderService Test Suite")
class HistoricalDataLoaderServiceTest {

    @Mock
    private WebClient atmoApiWebClient;

    @Mock
    private AirQualityStationRepository stationRepository;

    @Mock
    private AirQualityMeasurementRepository measurementRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private HistoricalDataLoaderService historicalDataLoaderService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        historicalDataLoaderService = new HistoricalDataLoaderService(
                atmoApiWebClient,
                stationRepository,
                measurementRepository,
                cityRepository,
                objectMapper
        );
    }

    @Nested
    @DisplayName("loadLast30DaysHistory tests")
    class LoadLast30DaysHistoryTests {

        @Test
        @DisplayName("should load and save historical data")
        void shouldLoadAndSaveHistoricalData() {
            String jsonResponse = createValidJsonResponse();

            AirQualityStation station = createStation();
            City city = createCity();

            when(atmoApiWebClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(jsonResponse))
                    .thenReturn(Mono.just("{\"features\": []}"));

            when(stationRepository.findByCode("FR31002")).thenReturn(Optional.of(station));
            when(measurementRepository.existsByStationAndMeasuredAt(any(), any())).thenReturn(false);
            when(measurementRepository.save(any(AirQualityMeasurement.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            historicalDataLoaderService.loadLast30DaysHistory();

            verify(measurementRepository, atLeastOnce()).save(any(AirQualityMeasurement.class));
        }

        @Test
        @DisplayName("should skip existing measurements")
        void shouldSkipExistingMeasurements() {
            String jsonResponse = createValidJsonResponse();

            AirQualityStation station = createStation();

            when(atmoApiWebClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(jsonResponse))
                    .thenReturn(Mono.just("{\"features\": []}"));

            when(stationRepository.findByCode("FR31002")).thenReturn(Optional.of(station));
            when(measurementRepository.existsByStationAndMeasuredAt(any(), any())).thenReturn(true);

            historicalDataLoaderService.loadLast30DaysHistory();

            verify(measurementRepository, never()).save(any(AirQualityMeasurement.class));
        }

        @Test
        @DisplayName("should handle empty API response")
        void shouldHandleEmptyApiResponse() {
            when(atmoApiWebClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("{\"features\": []}"));

            historicalDataLoaderService.loadLast30DaysHistory();

            verify(measurementRepository, never()).save(any());
        }

        @Test
        @DisplayName("should create new station when not found")
        void shouldCreateNewStationWhenNotFound() {
            String jsonResponse = createValidJsonResponse();
            City city = createCity();

            when(atmoApiWebClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(jsonResponse))
                    .thenReturn(Mono.just("{\"features\": []}"));

            when(stationRepository.findByCode("FR31002")).thenReturn(Optional.empty());
            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(stationRepository.save(any(AirQualityStation.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(measurementRepository.existsByStationAndMeasuredAt(any(), any())).thenReturn(false);
            when(measurementRepository.save(any(AirQualityMeasurement.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            historicalDataLoaderService.loadLast30DaysHistory();

            verify(stationRepository).save(any(AirQualityStation.class));
        }

        @Test
        @DisplayName("should skip station when city not found")
        void shouldSkipStationWhenCityNotFound() {
            String jsonResponse = createValidJsonResponse();

            when(atmoApiWebClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(jsonResponse))
                    .thenReturn(Mono.just("{\"features\": []}"));

            when(stationRepository.findByCode("FR31002")).thenReturn(Optional.empty());
            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.empty());

            historicalDataLoaderService.loadLast30DaysHistory();

            verify(measurementRepository, never()).save(any());
        }

        @Test
        @DisplayName("should handle API error gracefully")
        void shouldHandleApiErrorGracefully() {
            when(atmoApiWebClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.error(new RuntimeException("API Error")));

            historicalDataLoaderService.loadLast30DaysHistory();

            verify(measurementRepository, never()).save(any());
        }

        @Test
        @DisplayName("should save measurements with correct pollutant values")
        void shouldSaveMeasurementsWithCorrectPollutantValues() {
            String jsonResponse = createMultiPollutantJsonResponse();

            AirQualityStation station = createStation();

            when(atmoApiWebClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(jsonResponse))
                    .thenReturn(Mono.just("{\"features\": []}"));

            when(stationRepository.findByCode("FR31002")).thenReturn(Optional.of(station));
            when(measurementRepository.existsByStationAndMeasuredAt(any(), any())).thenReturn(false);

            ArgumentCaptor<AirQualityMeasurement> captor = ArgumentCaptor.forClass(AirQualityMeasurement.class);
            when(measurementRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            historicalDataLoaderService.loadLast30DaysHistory();

            AirQualityMeasurement saved = captor.getValue();
            assertThat(saved.getPm10()).isEqualTo(25.0);
            assertThat(saved.getNo2()).isEqualTo(15.0);
        }

        @Test
        @DisplayName("should handle invalid JSON structure")
        void shouldHandleInvalidJsonStructure() {
            when(atmoApiWebClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just("{\"invalid\": \"json\"}"));

            historicalDataLoaderService.loadLast30DaysHistory();

            verify(measurementRepository, never()).save(any());
        }
    }

    // Helper methods
    private String createValidJsonResponse() {
        return """
            {
                "features": [
                    {
                        "attributes": {
                            "code_station": "FR31002",
                            "nom_station": "Montpellier Centre",
                            "nom_poll": "PM10",
                            "valeur": 25.0,
                            "date_debut": 1704067200000,
                            "influence": "urbain",
                            "insee_com": 34172
                        }
                    }
                ]
            }
            """;
    }

    private String createMultiPollutantJsonResponse() {
        return """
            {
                "features": [
                    {
                        "attributes": {
                            "code_station": "FR31002",
                            "nom_station": "Montpellier Centre",
                            "nom_poll": "PM10",
                            "valeur": 25.0,
                            "date_debut": 1704067200000,
                            "insee_com": 34172
                        }
                    },
                    {
                        "attributes": {
                            "code_station": "FR31002",
                            "nom_station": "Montpellier Centre",
                            "nom_poll": "NO2",
                            "valeur": 15.0,
                            "date_debut": 1704067200000,
                            "insee_com": 34172
                        }
                    }
                ]
            }
            """;
    }

    private AirQualityStation createStation() {
        AirQualityStation station = new AirQualityStation();
        station.setId(1L);
        station.setCode("FR31002");
        station.setName("Montpellier Centre");
        return station;
    }

    private City createCity() {
        City city = new City();
        city.setId(1L);
        city.setName("Montpellier");
        city.setInseeCode("34172");
        city.setAreaCode("34");
        return city;
    }
}
