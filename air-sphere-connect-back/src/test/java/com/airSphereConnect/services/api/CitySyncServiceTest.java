package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiCityResponseDto;
import com.airSphereConnect.dtos.response.CentreDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.DepartmentRepository;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CitySyncService Test Suite")
class CitySyncServiceTest {

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
    private DepartmentRepository departmentRepository;

    private CitySyncService citySyncService;

    @BeforeEach
    void setUp() {
        citySyncService = new CitySyncService(webClient, cityRepository, departmentRepository);
        ReflectionTestUtils.setField(citySyncService, "enabled", true);
        ReflectionTestUtils.setField(citySyncService, "syncIntervalHours", 720);
    }

    @Nested
    @DisplayName("Interface methods tests")
    class InterfaceMethodsTests {

        @Test
        @DisplayName("getServiceName should return CITY")
        void getServiceName_shouldReturnCity() {
            assertThat(citySyncService.getServiceName()).isEqualTo("CITY");
        }

        @Test
        @DisplayName("isEnabled should return true when enabled and no errors")
        void isEnabled_shouldReturnTrue() {
            assertThat(citySyncService.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("isEnabled should return false when disabled")
        void isEnabled_shouldReturnFalseWhenDisabled() {
            ReflectionTestUtils.setField(citySyncService, "enabled", false);
            assertThat(citySyncService.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("getSyncInterval should return configured duration")
        void getSyncInterval_shouldReturnConfiguredDuration() {
            assertThat(citySyncService.getSyncInterval()).isEqualTo(Duration.ofHours(720));
        }

        @Test
        @DisplayName("getLastSync should return null initially")
        void getLastSync_shouldReturnNullInitially() {
            assertThat(citySyncService.getLastSync()).isNull();
        }

        @Test
        @DisplayName("getConsecutiveErrors should return 0 initially")
        void getConsecutiveErrors_shouldReturnZeroInitially() {
            assertThat(citySyncService.getConsecutiveErrors()).isZero();
        }
    }

    @Nested
    @DisplayName("syncData tests")
    class SyncDataTests {

        @Test
        @DisplayName("syncData should update lastSync on success")
        void syncData_shouldUpdateLastSyncOnSuccess() {
            Department dept = new Department();
            dept.setCode("34");
            dept.setName("Hérault");

            ApiCityResponseDto dto = new ApiCityResponseDto(
                    "34172", "Montpellier", List.of("34000"),
                    "243400017",
                    new CentreDto("Point", new Double[]{3.8767, 43.6109}),
                    "34", 290053
            );

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiCityResponseDto.class)).thenReturn(Flux.just(dto));
            when(departmentRepository.findAll()).thenReturn(List.of(dept));
            when(cityRepository.findByDepartmentIn(any())).thenReturn(Collections.emptyList());
            when(cityRepository.saveAll(any())).thenReturn(Collections.emptyList());

            citySyncService.syncData();

            assertThat(citySyncService.getLastSync()).isNotNull();
            assertThat(citySyncService.getConsecutiveErrors()).isZero();
        }

        @Test
        @DisplayName("syncData should increment errors on failure")
        void syncData_shouldIncrementErrorsOnFailure() {
            when(webClient.get()).thenThrow(new RuntimeException("API Error"));

            assertThatThrownBy(() -> citySyncService.syncData())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Sync failed for CITY");

            assertThat(citySyncService.getConsecutiveErrors()).isEqualTo(1);
        }

        @Test
        @DisplayName("isEnabled should return false after 3 consecutive errors")
        void isEnabled_shouldReturnFalseAfter3Errors() {
            when(webClient.get()).thenThrow(new RuntimeException("API Error"));

            for (int i = 0; i < 3; i++) {
                try {
                    citySyncService.syncData();
                } catch (Exception ignored) {}
            }

            assertThat(citySyncService.isEnabled()).isFalse();
        }
    }

    @Nested
    @DisplayName("importCitiesOccitanie tests")
    class ImportCitiesTests {

        @Test
        @DisplayName("should create new cities")
        void importCities_shouldCreateNewCities() {
            Department dept = new Department();
            dept.setCode("34");
            dept.setName("Hérault");

            ApiCityResponseDto dto = new ApiCityResponseDto(
                    "34172", "Montpellier", List.of("34000"),
                    "243400017",
                    new CentreDto("Point", new Double[]{3.8767, 43.6109}),
                    "34", 290053
            );

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiCityResponseDto.class)).thenReturn(Flux.just(dto));
            when(departmentRepository.findAll()).thenReturn(List.of(dept));
            when(cityRepository.findByDepartmentIn(any())).thenReturn(Collections.emptyList());
            when(cityRepository.saveAll(any())).thenReturn(Collections.emptyList());

            citySyncService.importCitiesOccitanie();

            verify(cityRepository).saveAll(any());
        }

        @Test
        @DisplayName("should return existing city when already exists")
        void importCities_shouldReturnExistingCity() {
            Department dept = new Department();
            dept.setCode("34");
            dept.setName("Hérault");

            City existingCity = new City();
            existingCity.setName("Montpellier");
            existingCity.setDepartment(dept);

            ApiCityResponseDto dto = new ApiCityResponseDto(
                    "34172", "Montpellier", List.of("34000"),
                    "243400017",
                    new CentreDto("Point", new Double[]{3.8767, 43.6109}),
                    "34", 290053
            );

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiCityResponseDto.class)).thenReturn(Flux.just(dto));
            when(departmentRepository.findAll()).thenReturn(List.of(dept));
            when(cityRepository.findByDepartmentIn(any())).thenReturn(List.of(existingCity));
            when(cityRepository.saveAll(any())).thenReturn(Collections.emptyList());

            citySyncService.importCitiesOccitanie();

            verify(cityRepository).saveAll(any());
        }

        @Test
        @DisplayName("should skip cities with unknown department")
        void importCities_shouldSkipUnknownDepartment() {
            ApiCityResponseDto dto = new ApiCityResponseDto(
                    "99999", "Unknown City", List.of("99000"),
                    "999999999",
                    new CentreDto("Point", new Double[]{0.0, 0.0}),
                    "99", 1000
            );

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiCityResponseDto.class)).thenReturn(Flux.just(dto));
            when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
            when(cityRepository.saveAll(any())).thenReturn(Collections.emptyList());

            citySyncService.importCitiesOccitanie();

            verify(cityRepository).saveAll(argThat(list -> ((List<?>) list).isEmpty()));
        }

        @Test
        @DisplayName("should do nothing when API returns empty list")
        void importCities_shouldDoNothingWhenEmpty() {
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiCityResponseDto.class)).thenReturn(Flux.empty());

            citySyncService.importCitiesOccitanie();

            verify(cityRepository, never()).saveAll(any());
        }
    }
}
