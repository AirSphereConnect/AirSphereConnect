package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiDepartmentResponseDto;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.ApiDepartmentMapper;
import com.airSphereConnect.repositories.DepartmentRepository;
import com.airSphereConnect.repositories.RegionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentSyncService {

    private final WebClient populationApiWebClient;
    private final DepartmentRepository departmentRepository;
    private final RegionRepository regionRepository;

    public DepartmentSyncService(WebClient populationApiWebClient, WebClient populationApiWebClient1, DepartmentRepository departmentRepository, RegionRepository regionRepository) {
        this.populationApiWebClient = populationApiWebClient1;
        this.departmentRepository = departmentRepository;
        this.regionRepository = regionRepository;
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
