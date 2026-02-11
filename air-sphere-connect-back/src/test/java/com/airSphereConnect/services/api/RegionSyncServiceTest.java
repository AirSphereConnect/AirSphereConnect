package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiRegionResponseDto;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.repositories.RegionRepository;
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
@DisplayName("RegionSyncService Test Suite")
class RegionSyncServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private RegionRepository regionRepository;

    private RegionSyncService regionSyncService;

    @BeforeEach
    void setUp() {
        regionSyncService = new RegionSyncService(webClient, regionRepository);
        ReflectionTestUtils.setField(regionSyncService, "enabled", true);
        ReflectionTestUtils.setField(regionSyncService, "syncIntervalHours", 720);
    }

    @Nested
    @DisplayName("Interface methods tests")
    class InterfaceMethodsTests {

        @Test
        @DisplayName("getServiceName should return REGION")
        void getServiceName_shouldReturnRegion() {
            assertThat(regionSyncService.getServiceName()).isEqualTo("REGION");
        }

        @Test
        @DisplayName("isEnabled should return true when enabled and no errors")
        void isEnabled_shouldReturnTrue() {
            assertThat(regionSyncService.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("isEnabled should return false when disabled")
        void isEnabled_shouldReturnFalseWhenDisabled() {
            ReflectionTestUtils.setField(regionSyncService, "enabled", false);
            assertThat(regionSyncService.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("getSyncInterval should return configured duration")
        void getSyncInterval_shouldReturnConfiguredDuration() {
            assertThat(regionSyncService.getSyncInterval()).isEqualTo(Duration.ofHours(720));
        }

        @Test
        @DisplayName("getLastSync should return null initially")
        void getLastSync_shouldReturnNullInitially() {
            assertThat(regionSyncService.getLastSync()).isNull();
        }

        @Test
        @DisplayName("getConsecutiveErrors should return 0 initially")
        void getConsecutiveErrors_shouldReturnZeroInitially() {
            assertThat(regionSyncService.getConsecutiveErrors()).isZero();
        }
    }

    @Nested
    @DisplayName("syncData tests")
    class SyncDataTests {

        @Test
        @DisplayName("syncData should update lastSync on success")
        void syncData_shouldUpdateLastSyncOnSuccess() {
            // Setup WebClient mock chain
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiRegionResponseDto.class))
                    .thenReturn(Flux.just(new ApiRegionResponseDto("76", "Occitanie")));
            when(regionRepository.findAll()).thenReturn(Collections.emptyList());
            when(regionRepository.saveAll(any())).thenReturn(Collections.emptyList());

            regionSyncService.syncData();

            assertThat(regionSyncService.getLastSync()).isNotNull();
            assertThat(regionSyncService.getConsecutiveErrors()).isZero();
        }

        @Test
        @DisplayName("syncData should increment errors on failure")
        void syncData_shouldIncrementErrorsOnFailure() {
            when(webClient.get()).thenThrow(new RuntimeException("API Error"));

            assertThatThrownBy(() -> regionSyncService.syncData())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Sync failed for REGION");

            assertThat(regionSyncService.getConsecutiveErrors()).isEqualTo(1);
        }

        @Test
        @DisplayName("isEnabled should return false after 3 consecutive errors")
        void isEnabled_shouldReturnFalseAfter3Errors() {
            when(webClient.get()).thenThrow(new RuntimeException("API Error"));

            for (int i = 0; i < 3; i++) {
                try {
                    regionSyncService.syncData();
                } catch (Exception ignored) {}
            }

            assertThat(regionSyncService.isEnabled()).isFalse();
            assertThat(regionSyncService.getConsecutiveErrors()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("importRegions tests")
    class ImportRegionsTests {

        @Test
        @DisplayName("should create new regions")
        void importRegions_shouldCreateNewRegions() {
            ApiRegionResponseDto dto = new ApiRegionResponseDto("76", "Occitanie");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiRegionResponseDto.class)).thenReturn(Flux.just(dto));
            when(regionRepository.findAll()).thenReturn(Collections.emptyList());
            when(regionRepository.saveAll(any())).thenReturn(Collections.emptyList());

            regionSyncService.importRegions();

            verify(regionRepository).saveAll(any());
        }

        @Test
        @DisplayName("should update existing regions")
        void importRegions_shouldUpdateExistingRegions() {
            ApiRegionResponseDto dto = new ApiRegionResponseDto("76", "Occitanie Updated");

            Region existingRegion = new Region();
            existingRegion.setCode("76");
            existingRegion.setName("Occitanie");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiRegionResponseDto.class)).thenReturn(Flux.just(dto));
            when(regionRepository.findAll()).thenReturn(List.of(existingRegion));
            when(regionRepository.saveAll(any())).thenReturn(Collections.emptyList());

            regionSyncService.importRegions();

            assertThat(existingRegion.getName()).isEqualTo("Occitanie Updated");
            verify(regionRepository).saveAll(any());
        }

        @Test
        @DisplayName("should do nothing when API returns empty list")
        void importRegions_shouldDoNothingWhenEmpty() {
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiRegionResponseDto.class)).thenReturn(Flux.empty());

            regionSyncService.importRegions();

            verify(regionRepository, never()).saveAll(any());
        }
    }
}
