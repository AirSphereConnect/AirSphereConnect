package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiPopulationResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Population;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.PopulationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PopulationSyncService Test Suite")
class PopulationSyncServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private PopulationRepository populationRepository;

    private PopulationSyncService populationSyncService;

    @BeforeEach
    void setUp() {
        populationSyncService = new PopulationSyncService(webClient, cityRepository, populationRepository);
        ReflectionTestUtils.setField(populationSyncService, "enabled", true);
        ReflectionTestUtils.setField(populationSyncService, "syncIntervalHours", 8760);
    }

    @Nested
    @DisplayName("Interface methods tests")
    class InterfaceMethodsTests {

        @Test
        @DisplayName("getServiceName should return POPULATION")
        void getServiceName_shouldReturnPopulation() {
            assertThat(populationSyncService.getServiceName()).isEqualTo("POPULATION");
        }

        @Test
        @DisplayName("isEnabled should return true when enabled and no errors")
        void isEnabled_shouldReturnTrue() {
            assertThat(populationSyncService.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("getSyncInterval should return configured duration")
        void getSyncInterval_shouldReturnConfiguredDuration() {
            assertThat(populationSyncService.getSyncInterval()).isEqualTo(Duration.ofHours(8760));
        }

        @Test
        @DisplayName("getLastSync should return null initially")
        void getLastSync_shouldReturnNullInitially() {
            assertThat(populationSyncService.getLastSync()).isNull();
        }

        @Test
        @DisplayName("getConsecutiveErrors should return 0 initially")
        void getConsecutiveErrors_shouldReturnZeroInitially() {
            assertThat(populationSyncService.getConsecutiveErrors()).isZero();
        }
    }

    @Nested
    @DisplayName("syncData tests")
    class SyncDataTests {

        @Test
        @DisplayName("syncData should update lastSync on success")
        void syncData_shouldUpdateLastSyncOnSuccess() {
            City city = new City();
            city.setName("Montpellier");
            city.setInseeCode("34172");
            city.setPopulations(new ArrayList<>());

            ApiPopulationResponseDto dto = new ApiPopulationResponseDto("34172", "Montpellier", 290053);

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiPopulationResponseDto.class)).thenReturn(Flux.just(dto));
            when(cityRepository.findByNameIgnoreCaseIn(any())).thenReturn(List.of(city));
            when(populationRepository.saveAll(any())).thenReturn(Collections.emptyList());
            when(cityRepository.saveAll(any())).thenReturn(Collections.emptyList());

            populationSyncService.syncData();

            assertThat(populationSyncService.getLastSync()).isNotNull();
            assertThat(populationSyncService.getConsecutiveErrors()).isZero();
        }

        @Test
        @DisplayName("syncData should increment errors on failure")
        void syncData_shouldIncrementErrorsOnFailure() {
            when(webClient.get()).thenThrow(new RuntimeException("API Error"));

            assertThatThrownBy(() -> populationSyncService.syncData())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Sync failed for POPULATION");

            assertThat(populationSyncService.getConsecutiveErrors()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("importPopulations tests")
    class ImportPopulationsTests {

        @Test
        @DisplayName("should create new population records")
        void importPopulations_shouldCreateNewPopulations() {
            City city = new City();
            city.setName("Montpellier");
            city.setInseeCode("34172");
            city.setPopulations(new ArrayList<>());

            ApiPopulationResponseDto dto = new ApiPopulationResponseDto("34172", "Montpellier", 290053);

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiPopulationResponseDto.class)).thenReturn(Flux.just(dto));
            when(cityRepository.findByNameIgnoreCaseIn(any())).thenReturn(List.of(city));
            when(populationRepository.saveAll(any())).thenReturn(Collections.emptyList());
            when(cityRepository.saveAll(any())).thenReturn(Collections.emptyList());

            populationSyncService.importPopulations();

            verify(populationRepository).saveAll(any());
            verify(cityRepository).saveAll(any());
            assertThat(city.getPopulation()).isEqualTo(290053);
        }

        @Test
        @DisplayName("should update existing population for current year")
        void importPopulations_shouldUpdateExistingPopulation() {
            int currentYear = Year.now().getValue();

            Population existingPop = new Population();
            existingPop.setYear(currentYear);
            existingPop.setPopulation(280000);

            City city = new City();
            city.setName("Montpellier");
            city.setInseeCode("34172");
            city.setPopulations(new ArrayList<>(List.of(existingPop)));
            existingPop.setCity(city);

            ApiPopulationResponseDto dto = new ApiPopulationResponseDto("34172", "Montpellier", 290053);

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiPopulationResponseDto.class)).thenReturn(Flux.just(dto));
            when(cityRepository.findByNameIgnoreCaseIn(any())).thenReturn(List.of(city));
            when(populationRepository.saveAll(any())).thenReturn(Collections.emptyList());
            when(cityRepository.saveAll(any())).thenReturn(Collections.emptyList());

            populationSyncService.importPopulations();

            assertThat(existingPop.getPopulation()).isEqualTo(290053);
            verify(populationRepository).saveAll(any());
        }

        @Test
        @DisplayName("should skip cities not found in database")
        void importPopulations_shouldSkipUnknownCities() {
            ApiPopulationResponseDto dto = new ApiPopulationResponseDto("99999", "Unknown City", 1000);

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiPopulationResponseDto.class)).thenReturn(Flux.just(dto));
            when(cityRepository.findByNameIgnoreCaseIn(any())).thenReturn(Collections.emptyList());
            when(populationRepository.saveAll(any())).thenReturn(Collections.emptyList());
            when(cityRepository.saveAll(any())).thenReturn(Collections.emptyList());

            populationSyncService.importPopulations();

            verify(populationRepository).saveAll(argThat(list -> ((List<?>) list).isEmpty()));
        }

        @Test
        @DisplayName("should do nothing when API returns empty list")
        void importPopulations_shouldDoNothingWhenEmpty() {
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiPopulationResponseDto.class)).thenReturn(Flux.empty());

            populationSyncService.importPopulations();

            verify(populationRepository, never()).saveAll(any());
        }
    }
}
