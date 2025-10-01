package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiCityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Population;
import com.airSphereConnect.mapper.ApiCityMapper;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.DepartmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitySyncService {

    private static final String SOURCE_URL = "/communes?codeRegion=76&fields=code,nom,codesPostaux,codeEpci," +
            "codeDepartement,centre,population";

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
                .uri(SOURCE_URL)
                .retrieve()
                .bodyToFlux(ApiCityResponseDto.class)
                .collectList()
                .block();

        if (cities == null || cities.isEmpty()) return;

        // Mapping departments by their code for quick access
        Map<String, Department> departmentMap = departmentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Department::getCode, d -> d));

        List<City> cityEntities = cities.stream()
                .filter(dto -> departmentMap.containsKey(dto.departmentCode()))
                .map(dto -> {
                    Department department = departmentMap.get(dto.departmentCode());

                    // check if city already exists
                   return cityRepository.findByNameIgnoreCaseAndDepartment(dto.name(), department)
                            .orElseGet(() -> ApiCityMapper.toEntity(dto, department));

                })
                .toList();

        // save all cities
        cityRepository.saveAll(cityEntities);
    }
}
