package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.AirQualityDailyMeasureResponseDto;
import com.airSphereConnect.dtos.response.AirQualityIndexMeasureResponseDto;
import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.mapper.api.ApiAirQualityMapper;
import com.airSphereConnect.repositories.AirQualityIndexRepository;
import com.airSphereConnect.repositories.AirQualityMeasurementRepository;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiAirQualityService Test Suite")
class ApiAirQualityServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private AirQualityStationRepository stationRepository;

    @Mock
    private AirQualityMeasurementRepository measurementRepository;

    @Mock
    private AirQualityIndexRepository indexRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private ApiAirQualityMapper mapper;

    private ObjectMapper objectMapper;

    private ApiAirQualityService apiAirQualityService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        apiAirQualityService = new ApiAirQualityService(
                webClient, stationRepository, measurementRepository,
                indexRepository, cityRepository, mapper, objectMapper
        );
        ReflectionTestUtils.setField(apiAirQualityService, "enabled", true);
        ReflectionTestUtils.setField(apiAirQualityService, "syncIntervalHours", 12);
    }

    @Nested
    @DisplayName("Interface methods tests")
    class InterfaceMethodsTests {

        @Test
        @DisplayName("getServiceName should return AIR_QUALITY")
        void getServiceName_shouldReturnAirQuality() {
            assertThat(apiAirQualityService.getServiceName()).isEqualTo("AIR_QUALITY");
        }

        @Test
        @DisplayName("isEnabled should return true when enabled and no errors")
        void isEnabled_shouldReturnTrue() {
            assertThat(apiAirQualityService.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("isEnabled should return false when disabled")
        void isEnabled_shouldReturnFalseWhenDisabled() {
            ReflectionTestUtils.setField(apiAirQualityService, "enabled", false);
            assertThat(apiAirQualityService.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("getSyncInterval should return configured duration")
        void getSyncInterval_shouldReturnConfiguredDuration() {
            assertThat(apiAirQualityService.getSyncInterval()).isEqualTo(Duration.ofHours(12));
        }

        @Test
        @DisplayName("getLastSync should return null initially")
        void getLastSync_shouldReturnNullInitially() {
            assertThat(apiAirQualityService.getLastSync()).isNull();
        }

        @Test
        @DisplayName("getConsecutiveErrors should return 0 initially")
        void getConsecutiveErrors_shouldReturnZeroInitially() {
            assertThat(apiAirQualityService.getConsecutiveErrors()).isZero();
        }
    }

    @Nested
    @DisplayName("fillPollutant tests")
    class FillPollutantTests {

        @Test
        @DisplayName("should fill PM10 correctly")
        void fillPollutant_shouldFillPm10() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "PM10", 25.5);
            assertThat(measurement.getPm10()).isEqualTo(25.5);
        }

        @Test
        @DisplayName("should fill PM2.5 correctly")
        void fillPollutant_shouldFillPm25() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "PM2.5", 12.3);
            assertThat(measurement.getPm25()).isEqualTo(12.3);
        }

        @Test
        @DisplayName("should fill PM25 correctly (alternative name)")
        void fillPollutant_shouldFillPm25Alternative() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "PM25", 15.0);
            assertThat(measurement.getPm25()).isEqualTo(15.0);
        }

        @Test
        @DisplayName("should fill NO2 correctly")
        void fillPollutant_shouldFillNo2() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "NO2", 40.0);
            assertThat(measurement.getNo2()).isEqualTo(40.0);
        }

        @Test
        @DisplayName("should fill O3 correctly")
        void fillPollutant_shouldFillO3() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "O3", 80.0);
            assertThat(measurement.getO3()).isEqualTo(80.0);
        }

        @Test
        @DisplayName("should fill SO2 correctly")
        void fillPollutant_shouldFillSo2() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "SO2", 10.0);
            assertThat(measurement.getSo2()).isEqualTo(10.0);
        }

        @Test
        @DisplayName("should handle null pollutant name")
        void fillPollutant_shouldHandleNullName() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, null, 10.0);
            assertThat(measurement.getPm10()).isNull();
            assertThat(measurement.getPm25()).isNull();
            assertThat(measurement.getNo2()).isNull();
            assertThat(measurement.getO3()).isNull();
            assertThat(measurement.getSo2()).isNull();
        }

        @Test
        @DisplayName("should handle null value")
        void fillPollutant_shouldHandleNullValue() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "PM10", null);
            assertThat(measurement.getPm10()).isNull();
        }

        @Test
        @DisplayName("should handle unknown pollutant")
        void fillPollutant_shouldHandleUnknownPollutant() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "UNKNOWN", 10.0);
            assertThat(measurement.getPm10()).isNull();
            assertThat(measurement.getPm25()).isNull();
            assertThat(measurement.getNo2()).isNull();
            assertThat(measurement.getO3()).isNull();
            assertThat(measurement.getSo2()).isNull();
        }

        @Test
        @DisplayName("should handle case insensitive pollutant names")
        void fillPollutant_shouldHandleCaseInsensitive() {
            AirQualityMeasurement measurement = new AirQualityMeasurement();
            apiAirQualityService.fillPollutant(measurement, "pm10", 30.0);
            assertThat(measurement.getPm10()).isEqualTo(30.0);
        }
    }

    @Nested
    @DisplayName("syncData tests")
    class SyncDataTests {

        @Test
        @DisplayName("syncData should update lastSync on success with empty data")
        void syncData_shouldUpdateLastSyncOnSuccessWithEmptyData() {
            // Mock WebClient to return empty JSON
            String emptyMeasuresJson = "{\"features\":[]}";
            String emptyIndexJson = "{\"features\":[]}";

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(emptyMeasuresJson))
                    .thenReturn(Mono.just(emptyIndexJson));

            apiAirQualityService.syncData();

            assertThat(apiAirQualityService.getLastSync()).isNotNull();
            assertThat(apiAirQualityService.getConsecutiveErrors()).isZero();
        }

        @Test
        @DisplayName("syncData should handle fetch errors gracefully")
        void syncData_shouldHandleFetchErrorsGracefully() {
            // fetchJson catches exceptions internally and returns null
            // so sync completes with 0 measures but no error count
            when(webClient.get()).thenThrow(new RuntimeException("API Error"));

            apiAirQualityService.syncData();

            // Sync completes (no exception propagated)
            assertThat(apiAirQualityService.getLastSync()).isNotNull();
        }

        @Test
        @DisplayName("syncData should process measures and indices")
        void syncData_shouldProcessMeasuresAndIndices() {
            String measuresJson = """
                {
                    "features": [
                        {
                            "properties": {
                                "code_station": "FR44001",
                                "nom_station": "Montpellier",
                                "nom_poll": "PM10",
                                "valeur": 25.5,
                                "unite": "µg/m³",
                                "insee_com": 34172
                            }
                        }
                    ]
                }
                """;
            String indexJson = """
                {
                    "features": [
                        {
                            "attributes": {
                                "code_zone": "34172",
                                "lib_zone": "Montpellier",
                                "code_qual": 2,
                                "lib_qual": "Bon",
                                "coul_qual": "#50F0E6",
                                "source": "ATMO"
                            }
                        }
                    ]
                }
                """;

            AirQualityStation station = new AirQualityStation();
            station.setCode("FR44001");
            City city = new City();
            city.setName("Montpellier");
            city.setInseeCode("34172");
            station.setCity(city);

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(measuresJson))
                    .thenReturn(Mono.just(indexJson));
            when(stationRepository.findByCode("FR44001")).thenReturn(Optional.of(station));
            when(measurementRepository.existsByStationAndMeasuredAt(any(), any())).thenReturn(false);
            when(indexRepository.findByAreaCodeAndMeasuredAt(anyString(), any())).thenReturn(Optional.empty());
            when(mapper.toEntity(any(AirQualityIndexMeasureResponseDto.class))).thenReturn(new AirQualityIndex());

            apiAirQualityService.syncData();

            assertThat(apiAirQualityService.getLastSync()).isNotNull();
            verify(measurementRepository).save(any(AirQualityMeasurement.class));
            verify(indexRepository).save(any(AirQualityIndex.class));
        }

        @Test
        @DisplayName("syncData should skip existing measurements")
        void syncData_shouldSkipExistingMeasurements() {
            String measuresJson = """
                {
                    "features": [
                        {
                            "properties": {
                                "code_station": "FR44001",
                                "nom_station": "Montpellier",
                                "nom_poll": "PM10",
                                "valeur": 25.5,
                                "insee_com": 34172
                            }
                        }
                    ]
                }
                """;
            String emptyIndexJson = "{\"features\":[]}";

            AirQualityStation station = new AirQualityStation();
            station.setCode("FR44001");
            City city = new City();
            station.setCity(city);

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(measuresJson))
                    .thenReturn(Mono.just(emptyIndexJson));
            when(stationRepository.findByCode("FR44001")).thenReturn(Optional.of(station));
            when(measurementRepository.existsByStationAndMeasuredAt(any(), any())).thenReturn(true);

            apiAirQualityService.syncData();

            verify(measurementRepository, never()).save(any(AirQualityMeasurement.class));
        }

        @Test
        @DisplayName("syncData should update existing index")
        void syncData_shouldUpdateExistingIndex() {
            String emptyMeasuresJson = "{\"features\":[]}";
            String indexJson = """
                {
                    "features": [
                        {
                            "attributes": {
                                "code_zone": "34172",
                                "lib_zone": "Montpellier",
                                "code_qual": 2,
                                "lib_qual": "Bon",
                                "coul_qual": "#50F0E6",
                                "source": "ATMO"
                            }
                        }
                    ]
                }
                """;

            AirQualityIndex existingIndex = new AirQualityIndex();
            existingIndex.setAreaCode("34172");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(emptyMeasuresJson))
                    .thenReturn(Mono.just(indexJson));
            when(indexRepository.findByAreaCodeAndMeasuredAt(anyString(), any()))
                    .thenReturn(Optional.of(existingIndex));

            apiAirQualityService.syncData();

            verify(indexRepository).save(existingIndex);
            assertThat(existingIndex.getQualityIndex()).isEqualTo(2);
            assertThat(existingIndex.getQualityLabel()).isEqualTo("Bon");
        }

        @Test
        @DisplayName("syncData should create new station when not found")
        void syncData_shouldCreateNewStationWhenNotFound() {
            String measuresJson = """
                {
                    "features": [
                        {
                            "properties": {
                                "code_station": "FR44002",
                                "nom_station": "Toulouse",
                                "nom_poll": "NO2",
                                "valeur": 35.0,
                                "insee_com": 31555
                            }
                        }
                    ]
                }
                """;
            String emptyIndexJson = "{\"features\":[]}";

            City city = new City();
            city.setName("Toulouse");
            city.setInseeCode("31555");
            city.setAreaCode("31");

            AirQualityStation newStation = new AirQualityStation();
            newStation.setCode("FR44002");
            newStation.setCity(city);

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(measuresJson))
                    .thenReturn(Mono.just(emptyIndexJson));
            when(stationRepository.findByCode("FR44002")).thenReturn(Optional.empty());
            when(mapper.toEntity(any(AirQualityDailyMeasureResponseDto.class))).thenReturn(newStation);
            when(cityRepository.findByInseeCode("31555")).thenReturn(Optional.of(city));
            when(stationRepository.save(any())).thenReturn(newStation);
            when(measurementRepository.existsByStationAndMeasuredAt(any(), any())).thenReturn(false);

            apiAirQualityService.syncData();

            verify(stationRepository).save(any(AirQualityStation.class));
            verify(measurementRepository).save(any(AirQualityMeasurement.class));
        }

        @Test
        @DisplayName("syncData should handle null response")
        void syncData_shouldHandleNullResponse() {
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.empty());

            apiAirQualityService.syncData();

            // Should not throw and should complete
            assertThat(apiAirQualityService.getLastSync()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Alert detection tests")
    class AlertDetectionTests {

        @Test
        @DisplayName("should detect alert when quality index is high")
        void syncData_shouldDetectAlertWhenQualityIndexIsHigh() {
            String emptyMeasuresJson = "{\"features\":[]}";
            String indexJson = """
                {
                    "features": [
                        {
                            "attributes": {
                                "code_zone": "34172",
                                "lib_zone": "Montpellier",
                                "code_qual": 5,
                                "lib_qual": "Mauvais",
                                "coul_qual": "#FF0000",
                                "source": "ATMO"
                            }
                        }
                    ]
                }
                """;

            AirQualityIndex newIndex = new AirQualityIndex();

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToMono(String.class))
                    .thenReturn(Mono.just(emptyMeasuresJson))
                    .thenReturn(Mono.just(indexJson));
            when(indexRepository.findByAreaCodeAndMeasuredAt(anyString(), any()))
                    .thenReturn(Optional.empty());
            when(mapper.toEntity(any(AirQualityIndexMeasureResponseDto.class))).thenReturn(newIndex);

            apiAirQualityService.syncData();

            verify(indexRepository).save(any(AirQualityIndex.class));
        }
    }
}
