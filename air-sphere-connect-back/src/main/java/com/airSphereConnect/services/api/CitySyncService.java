package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiCityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.ApiCityMapper;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CitySyncService {
    private final WebClient webClient;
    private final CityRepository cityRepository;
    private final DepartmentRepository departmentRepository;

    public CitySyncService(WebClient populationApiWebClient, CityRepository cityRepository, DepartmentRepository departmentRepository) {
        this.webClient = populationApiWebClient;
        this.departmentRepository = departmentRepository;
        this.cityRepository = cityRepository;
    }

    public void importCitiesOccitanie() {
        List<ApiCityResponseDto> cities = webClient.get()
                .uri("/communes?codeRegion=76&fields=nom,codesPostaux,codeEpci,codeDepartement,centre")
                .retrieve()
                .bodyToFlux(ApiCityResponseDto.class)
                .collectList()
                .block();

        if (cities == null || cities.isEmpty()) return;

        Map<String, Department> departmentMap = departmentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Department::getCode, d -> d));

        List<City> cityEntities = cities.stream()
                .filter(dto -> departmentMap.containsKey(dto.departmentCode()))
                .map(dto -> {
                    Department department = departmentMap.get(dto.departmentCode());
                    return ApiCityMapper.toEntity(dto, department);
                }).toList();
        cityRepository.saveAll(cityEntities);
    }
}
