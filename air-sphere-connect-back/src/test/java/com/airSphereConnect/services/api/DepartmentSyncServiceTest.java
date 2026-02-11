package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiDepartmentResponseDto;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.DepartmentRepository;
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
@DisplayName("DepartmentSyncService Test Suite")
class DepartmentSyncServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private RegionRepository regionRepository;

    private DepartmentSyncService departmentSyncService;

    @BeforeEach
    void setUp() {
        departmentSyncService = new DepartmentSyncService(webClient, departmentRepository, regionRepository);
        ReflectionTestUtils.setField(departmentSyncService, "enabled", true);
        ReflectionTestUtils.setField(departmentSyncService, "syncIntervalHours", 720);
    }

    @Nested
    @DisplayName("Interface methods tests")
    class InterfaceMethodsTests {

        @Test
        @DisplayName("getServiceName should return DEPARTMENT")
        void getServiceName_shouldReturnDepartment() {
            assertThat(departmentSyncService.getServiceName()).isEqualTo("DEPARTMENT");
        }

        @Test
        @DisplayName("isEnabled should return true when enabled and no errors")
        void isEnabled_shouldReturnTrue() {
            assertThat(departmentSyncService.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("getSyncInterval should return configured duration")
        void getSyncInterval_shouldReturnConfiguredDuration() {
            assertThat(departmentSyncService.getSyncInterval()).isEqualTo(Duration.ofHours(720));
        }

        @Test
        @DisplayName("getLastSync should return null initially")
        void getLastSync_shouldReturnNullInitially() {
            assertThat(departmentSyncService.getLastSync()).isNull();
        }

        @Test
        @DisplayName("getConsecutiveErrors should return 0 initially")
        void getConsecutiveErrors_shouldReturnZeroInitially() {
            assertThat(departmentSyncService.getConsecutiveErrors()).isZero();
        }
    }

    @Nested
    @DisplayName("syncData tests")
    class SyncDataTests {

        @Test
        @DisplayName("syncData should update lastSync on success")
        void syncData_shouldUpdateLastSyncOnSuccess() {
            Region region = new Region();
            region.setCode("76");
            region.setName("Occitanie");

            ApiDepartmentResponseDto dto = new ApiDepartmentResponseDto("34", "Hérault", "76");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiDepartmentResponseDto.class)).thenReturn(Flux.just(dto));
            when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
            when(regionRepository.findAll()).thenReturn(List.of(region));
            when(departmentRepository.saveAll(any())).thenReturn(Collections.emptyList());

            departmentSyncService.syncData();

            assertThat(departmentSyncService.getLastSync()).isNotNull();
            assertThat(departmentSyncService.getConsecutiveErrors()).isZero();
        }

        @Test
        @DisplayName("syncData should increment errors on failure")
        void syncData_shouldIncrementErrorsOnFailure() {
            when(webClient.get()).thenThrow(new RuntimeException("API Error"));

            assertThatThrownBy(() -> departmentSyncService.syncData())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Sync failed for DEPARTMENT");

            assertThat(departmentSyncService.getConsecutiveErrors()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("importDepartments tests")
    class ImportDepartmentsTests {

        @Test
        @DisplayName("should create new departments")
        void importDepartments_shouldCreateNewDepartments() {
            Region region = new Region();
            region.setCode("76");
            region.setName("Occitanie");

            ApiDepartmentResponseDto dto = new ApiDepartmentResponseDto("34", "Hérault", "76");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiDepartmentResponseDto.class)).thenReturn(Flux.just(dto));
            when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
            when(regionRepository.findAll()).thenReturn(List.of(region));
            when(departmentRepository.saveAll(any())).thenReturn(Collections.emptyList());

            departmentSyncService.importDepartments();

            verify(departmentRepository).saveAll(any());
        }

        @Test
        @DisplayName("should skip existing departments")
        void importDepartments_shouldSkipExistingDepartments() {
            Region region = new Region();
            region.setCode("76");
            region.setName("Occitanie");

            Department existingDept = new Department();
            existingDept.setCode("34");
            existingDept.setName("Hérault");
            existingDept.setRegion(region);

            ApiDepartmentResponseDto dto = new ApiDepartmentResponseDto("34", "Hérault", "76");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiDepartmentResponseDto.class)).thenReturn(Flux.just(dto));
            when(departmentRepository.findAll()).thenReturn(List.of(existingDept));
            when(regionRepository.findAll()).thenReturn(List.of(region));
            when(departmentRepository.saveAll(any())).thenReturn(Collections.emptyList());

            departmentSyncService.importDepartments();

            verify(departmentRepository).saveAll(argThat(list -> ((List<?>) list).isEmpty()));
        }

        @Test
        @DisplayName("should throw exception when region not found")
        void importDepartments_shouldThrowExceptionWhenRegionNotFound() {
            ApiDepartmentResponseDto dto = new ApiDepartmentResponseDto("34", "Hérault", "99");

            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiDepartmentResponseDto.class)).thenReturn(Flux.just(dto));
            when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
            when(regionRepository.findAll()).thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> departmentSyncService.importDepartments())
                    .isInstanceOf(GlobalException.ResourceNotFoundException.class)
                    .hasMessageContaining("Region with code 99 not found");
        }

        @Test
        @DisplayName("should do nothing when API returns empty list")
        void importDepartments_shouldDoNothingWhenEmpty() {
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            when(responseSpec.bodyToFlux(ApiDepartmentResponseDto.class)).thenReturn(Flux.empty());

            departmentSyncService.importDepartments();

            verify(departmentRepository, never()).saveAll(any());
        }
    }
}
