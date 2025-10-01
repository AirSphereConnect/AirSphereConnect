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

@Service
@Transactional
public class DepartmentSyncService {

    private final WebClient webClient;
    private final DepartmentRepository departmentRepository;
    private final RegionRepository regionRepository;

    public DepartmentSyncService(WebClient webClient, DepartmentRepository departmentRepository, RegionRepository regionRepository) {
        this.webClient = webClient;
        this.departmentRepository = departmentRepository;
        this.regionRepository = regionRepository;
    }

    public void importDepartments() {
        List<ApiDepartmentResponseDto> departments = webClient.get()
                .uri("/departements")
                .retrieve()
                .bodyToFlux(ApiDepartmentResponseDto.class)
                .collectList()
                .block();

        if(departments != null && !departments.isEmpty()) {
            List<Department> departmentsEntities = departments.stream()
                    .map(dto -> {
                        Region region =
                                regionRepository.findByCode(dto.regionCode()).orElseThrow( () -> new GlobalException.ResourceNotFoundException("Region with code " + dto.regionCode() + " not found."));
                        return ApiDepartmentMapper.toEntity(dto, region);
                    })
                    .toList();

            departmentRepository.saveAll(departmentsEntities);
        }
    }


}
