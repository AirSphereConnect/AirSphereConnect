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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitySyncService {

    // codeepci = areaCode
    private static final String SOURCE_URL = "/communes?codeRegion=76&fields=code,nom,codesPostaux,codeEpci," +
            "codeDepartement,centre,population";

    private final WebClient populationApiWebClient;
    private final CityRepository cityRepository;
    private final DepartmentRepository departmentRepository;

    public CitySyncService(WebClient populationApiWebClient, CityRepository cityRepository, DepartmentRepository departmentRepository) {
        this.populationApiWebClient = populationApiWebClient;
        this.departmentRepository = departmentRepository;
        this.cityRepository = cityRepository;
    }

    public void importCitiesOccitanie() {
        List<ApiCityResponseDto> cities = populationApiWebClient.get()
                .uri(SOURCE_URL)
                .retrieve()
                .bodyToFlux(ApiCityResponseDto.class)
                .collectList()
                .block();

        if (cities == null || cities.isEmpty()) return;

        // load all departments with Map
        Map<String, Department> departmentMap = departmentRepository.findAll()
                .stream()
                .collect(Collectors
                        .toMap(Department::getCode,
                                dep -> dep));


        // load all cities existing with department code
        // extract unique departmentCode
        List<String> departmentCodeList = cities.stream()
                .map(ApiCityResponseDto::departmentCode)
                .distinct()
                .toList();

        // get all departments with this code
        List<Department> departments = departmentCodeList.stream()
                .map(departmentMap::get)
                .filter(Objects::nonNull)
                .toList();


        // Load all cities in One request with key : cityName - codeDep
        Map<String, City> cityMap = cityRepository.findByDepartmentIn(departments)
                .stream()
                .collect(Collectors.toMap(
                        city -> city.getName() + city.getDepartment().getCode(),
                        city -> city
                ));


        List<City> cityEntities = cities.stream()
                .map(dto -> {
                    // retrieve department in map
                    Department department = departmentMap.get(dto.departmentCode());

                    if (department == null) return null;

                    // make key to search city
                    String key = dto.name() + dto.departmentCode();
                    City city = cityMap.get(key);

                    if (city != null) {
                        // return city
                        return city;
                    } else {
                        // new city via mapper
                        return ApiCityMapper.toEntity(dto, department);
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        cityRepository.saveAll(cityEntities);
    }
}
