package com.airsphereconnect.services.api;

import com.airsphereconnect.dtos.response.ApiCityResponseDto;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.Department;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.ApiCityMapper;
import com.airsphereconnect.repositories.CityRepository;
import com.airsphereconnect.repositories.DepartmentRepository;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitySyncService implements DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(CitySyncService.class);

    // codeepci = areaCode
    private static final String SOURCE_URL = "/communes?codeRegion=76&fields=code,nom,codesPostaux,codeEpci," +
            "codeDepartement,centre,population";

    private final WebClient populationApiWebClient;
    private final CityRepository cityRepository;
    private final DepartmentRepository departmentRepository;

    @Value("${app.api.city.enabled:true}")
    private boolean enabled;

    @Value("${app.api.city.sync-interval-hours:720}")
    private int syncIntervalHours; // 30 jours par défaut

    private LocalDateTime lastSync;
    private int consecutiveErrors = 0;

    public CitySyncService(WebClient populationApiWebClient, CityRepository cityRepository, DepartmentRepository departmentRepository) {
        this.populationApiWebClient = populationApiWebClient;
        this.departmentRepository = departmentRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public String getServiceName() {
        return "CITY";
    }

    @Override
    public void syncData() {
        log.info("🔄 Début synchronisation des villes...");
        try {
            importCitiesOccitanie();
            lastSync = LocalDateTime.now();
            consecutiveErrors = 0;
            log.info("✅ [CITY] Sync terminée");
        } catch (Exception e) {
            consecutiveErrors++;
            throw new GlobalException.SyncException("Sync failed for CITY", e);
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
