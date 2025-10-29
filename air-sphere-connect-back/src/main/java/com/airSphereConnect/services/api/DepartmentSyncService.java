package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiDepartmentResponseDto;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.ApiDepartmentMapper;
import com.airSphereConnect.repositories.DepartmentRepository;
import com.airSphereConnect.repositories.RegionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentSyncService implements DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentSyncService.class);

    private final WebClient populationApiWebClient;
    private final DepartmentRepository departmentRepository;
    private final RegionRepository regionRepository;

    @Value("${app.api.department.enabled:true}")
    private boolean enabled;

    @Value("${app.api.department.sync-interval-hours:720}")
    private int syncIntervalHours; // 30 jours par défaut

    private LocalDateTime lastSync;
    private int consecutiveErrors = 0;

    public DepartmentSyncService(WebClient populationApiWebClient, DepartmentRepository departmentRepository, RegionRepository regionRepository) {
        this.populationApiWebClient = populationApiWebClient;
        this.departmentRepository = departmentRepository;
        this.regionRepository = regionRepository;
    }

    @Override
    public String getServiceName() {
        return "DEPARTMENT";
    }

    @Override
    public void syncData() {
        log.info("🔄 Début synchronisation des départements...");
        try {
            importDepartments();
            lastSync = LocalDateTime.now();
            consecutiveErrors = 0;
            log.info("✅ [DEPARTMENT] Sync terminée");
        } catch (Exception e) {
            consecutiveErrors++;
            log.error("❌ [DEPARTMENT] Erreur sync (tentative {}/3) : {}", consecutiveErrors, e.getMessage(), e);
            throw new RuntimeException("Sync failed for DEPARTMENT", e);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled && consecutiveErrors < 3;
    }

    @Override
    public Duration getSyncInterval() {
        return Duration.ofHours(syncIntervalHours);
    }

    @Override
    public LocalDateTime getLastSync() {
        return lastSync;
    }

    @Override
    public int getConsecutiveErrors() {
        return consecutiveErrors;
    }


    // Retrieve all departments from API
    public void importDepartments() {
        List<ApiDepartmentResponseDto> departments = populationApiWebClient.get()
                .uri("/departements")
                .retrieve()
                .bodyToFlux(ApiDepartmentResponseDto.class)
                .collectList()
                .block();

        if (departments == null || departments.isEmpty()) return;

        // Allow to verify if a department already exists
        Map<String, Department> departmentMap = departmentRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Department::getCode,
                        dept -> dept,
                        (existing, replace) -> existing
                        ));

        // Load all regions in Unique request with Map for quick access
        Map<String, Region> regionMap = regionRepository.findAll().stream()
                .collect(
                        Collectors.toMap(Region::getCode,
                                region -> region));


        // Identify and create new department
        List<Department> newDepartments = departments.stream()

                // allow to keep only new departments
                .filter(dto -> !departmentMap.containsKey(dto.code()))
                .map(dto -> {

                    // retrieve region with map
                    Region region = regionMap.get(dto.regionCode());

                    // Validation
                    if (region == null) {
                        throw new GlobalException.ResourceNotFoundException(
                                "Region with code " + dto.regionCode() + " not found for department " + dto.name() +
                                        " (" + dto.code() + ")");
                    }
                    // create new department with mapper
                    return ApiDepartmentMapper.toEntity(dto, region);
                })
                .toList();

        // save only new department
        departmentRepository.saveAll(newDepartments);
    }
}
