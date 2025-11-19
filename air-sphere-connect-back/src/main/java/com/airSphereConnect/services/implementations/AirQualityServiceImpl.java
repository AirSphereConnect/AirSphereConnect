package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.response.AirQualityIndexResponseDto;
import com.airSphereConnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airSphereConnect.dtos.response.AirQualityStationResponseDto;
import com.airSphereConnect.dtos.response.AirQualityDataResponseDto;
import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.AirQualityMapper;
import com.airSphereConnect.repositories.AirQualityIndexRepository;
import com.airSphereConnect.repositories.AirQualityMeasurementRepository;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.services.AirQualityService;
import com.airSphereConnect.services.api.ApiAirQualityService;
import com.airSphereConnect.utils.AirQualityAlertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@Transactional(readOnly = true)
public class AirQualityServiceImpl implements AirQualityService {

    private static final Logger log = LoggerFactory.getLogger(ApiAirQualityService.class);

    private final AirQualityStationRepository stationRepository;
    private final AirQualityMeasurementRepository measurementRepository;
    private final AirQualityIndexRepository indexRepository;
    private final CityRepository cityRepository;
    private final AirQualityMapper mapper;

    public AirQualityServiceImpl (
            AirQualityStationRepository stationRepository,
            AirQualityMeasurementRepository measurementRepository,
            AirQualityIndexRepository indexRepository,
            CityRepository cityRepository,
            AirQualityMapper mapper) {
        this.stationRepository = stationRepository;
        this.measurementRepository = measurementRepository;
        this.indexRepository = indexRepository;
        this.cityRepository = cityRepository;
        this.mapper = mapper;
    }

    @Override
    public List<AirQualityStationResponseDto> getAllStations() {
        return stationRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public AirQualityMeasurementResponseDto getLatestMeasurementForCity(String cityName) {
        City city = findCityByName(cityName);

        AirQualityMeasurement measurement = null;

        // Chercher avec le code INSEE exact
        if (city.getInseeCode() != null) {
            measurement = measurementRepository
                    .findTopByStation_City_InseeCodeOrderByMeasuredAtDesc(city.getInseeCode())
                    .orElse(null);

            if (measurement != null) {
                return mapper.toDto(measurement);
            }
        }

        // Chercher dans le même areaCode (intercommunalité)
        if (city.getAreaCode() != null) {
            measurement = measurementRepository
                    .findTopByStation_City_AreaCodeOrderByMeasuredAtDesc(city.getAreaCode())
                    .orElse(null);

            if (measurement != null) {
                return mapper.toDto(measurement);
            }
        }

        // Fallback: Chercher dans le même département (2 premiers chiffres INSEE)
        if (city.getInseeCode() != null && city.getInseeCode().length() >= 2) {
            String departmentCode = city.getInseeCode().substring(0, 2);
            List<AirQualityMeasurement> deptMeasurements = measurementRepository
                    .findLatestByDepartmentCode(departmentCode);

            if (!deptMeasurements.isEmpty()) {
                String sourceCities = deptMeasurements.stream()
                    .map(m -> m.getStation().getCity().getName())
                    .distinct()
                    .limit(5)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

                return mapper.toDto(deptMeasurements.get(0), "department", sourceCities);
            }
        }

        throw new GlobalException.ResourceNotFoundException(
                "Aucune mesure trouvée pour: " + cityName);
    }

    @Override
    public AirQualityIndexResponseDto getLatestIndexQualityForCity(String cityName) {
        City city = findCityByName(cityName);

        if (city.getAreaCode() == null) {
            throw new GlobalException.ResourceNotFoundException(
                    "Aucun areaCode pour la ville: " + cityName);
        }

        AirQualityIndex index = indexRepository
                .findFirstByAreaCodeOrderByMeasuredAtDesc(
                        city.getAreaCode())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                        "Aucun indice ATMO trouvé pour: " + cityName));

        String alertMessage = AirQualityAlertUtils.determineAlertMessage(index.getQualityIndex());

        return mapper.toDto(index, alertMessage);
    }

    @Override
    public List<AirQualityMeasurementResponseDto> getMeasurementsHistoryForCity(
            String cityName,
            LocalDate startDate,
            LocalDate endDate) {

        City city = findCityByName(cityName);
        LocalDateTime[] period = getDateRange(startDate, endDate);

        List<AirQualityMeasurement> measurements = null;

        // Chercher avec code INSEE exact
        if (city.getInseeCode() != null) {
            measurements = measurementRepository
                    .findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                            city.getInseeCode(), period[0], period[1]);

            if (!measurements.isEmpty()) {
                return measurements.stream().map(mapper::toDto).toList();
            }
        }

        // Chercher avec areaCode
        if (city.getAreaCode() != null) {
            measurements = measurementRepository
                    .findByStation_City_AreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                            city.getAreaCode(), period[0], period[1]);

            if (!measurements.isEmpty()) {
                return measurements.stream().map(mapper::toDto).toList();
            }
        }

        // Fallback: Chercher dans le même département
        if (city.getInseeCode() != null && city.getInseeCode().length() >= 2) {
            String departmentCode = city.getInseeCode().substring(0, 2);
            measurements = measurementRepository
                    .findByDepartmentCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                            departmentCode, period[0], period[1]);

            if (!measurements.isEmpty()) {
                return measurements.stream().map(mapper::toDto).toList();
            }
        }

        return List.of();
    }

    @Override
    public List<AirQualityIndexResponseDto> getIndicesHistoryForCity(
            String cityName,
            LocalDate startDate,
            LocalDate endDate) {

        City city = findCityByName(cityName);

        if (city.getAreaCode() == null) {
            throw new GlobalException.ResourceNotFoundException(
                    "Aucun areaCode pour la ville: " + cityName);
        }

        LocalDateTime[] period = getDateRange(startDate, endDate);

        List<AirQualityIndex> indices = indexRepository
                .findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                        city.getAreaCode(), period[0], period[1]);

        return indices.stream()
                .map(index -> {
                    String alertMessage = AirQualityAlertUtils.determineAlertMessage(index.getQualityIndex());
                    return mapper.toDto(index, alertMessage);
                })
                .toList();
    }


    @Override
    public AirQualityDataResponseDto getCompleteDataForCity(String cityName) {
        City city = findCityByName(cityName);

        AirQualityDataResponseDto dto = new AirQualityDataResponseDto();
        dto.setCityId(city.getId());
        dto.setCityName(city.getName());
        dto.setPostalCode(city.getPostalCode());
        dto.setAreaCode(city.getAreaCode());

        // Indice ATMO
        if (city.getAreaCode() != null) {
            try {
                indexRepository
                        .findFirstByAreaCodeOrderByMeasuredAtDesc(
                                city.getAreaCode())
                        .ifPresent(index -> {
                            dto.setQualityIndex(index.getQualityIndex());
                            dto.setQualityLabel(index.getQualityLabel());
                            dto.setQualityColor(index.getQualityColor());
                            dto.setIndexMeasuredAt(index.getMeasuredAt());

                            String alertMessage = AirQualityAlertUtils.determineAlertMessage(index.getQualityIndex());
                            dto.setAlertMessage(alertMessage);

                            dto.setLatestIndex(mapper.toDto(index, alertMessage));
                        });
            } catch (NumberFormatException e) {
                log.error("Code EPCI invalide pour la ville : ", city.getName());
            }
        }

        List<AirQualityMeasurement> measurements = null;

        // Chercher avec code INSEE exact
        if (city.getInseeCode() != null) {
            measurements = measurementRepository.findByStation_City_InseeCodeOrderByMeasuredAtDesc(city.getInseeCode());
        }

        // Chercher avec areaCode
        if ((measurements == null || measurements.isEmpty()) && city.getAreaCode() != null) {
            measurements = measurementRepository.findByStation_City_AreaCodeOrderByMeasuredAtDesc(city.getAreaCode());
        }

        // Fallback: Chercher dans le même département
        if ((measurements == null || measurements.isEmpty()) &&
            city.getInseeCode() != null && city.getInseeCode().length() >= 2) {
            String departmentCode = city.getInseeCode().substring(0, 2);
            measurements = measurementRepository.findByDepartmentCodeOrderByMeasuredAtDesc(departmentCode);
        }

        if (measurements != null && !measurements.isEmpty()) {
            dto.setPm10(findFirstNonNull(measurements, AirQualityMeasurement::getPm10));
            dto.setPm25(findFirstNonNull(measurements, AirQualityMeasurement::getPm25));
            dto.setNo2(findFirstNonNull(measurements, AirQualityMeasurement::getNo2));
            dto.setO3(findFirstNonNull(measurements, AirQualityMeasurement::getO3));
            dto.setSo2(findFirstNonNull(measurements, AirQualityMeasurement::getSo2));

            dto.setPollutantsMeasuredAt(
                    measurements.stream()
                            .map(AirQualityMeasurement::getMeasuredAt)
                            .max(LocalDateTime::compareTo)
                            .orElse(null)
            );

            dto.setLatestMeasurement(mapper.toDto(measurements.get(0)));
        }

        dto.setMeasurementHistory(
                getMeasurementsHistoryForCity(cityName, null, null)
        );
        dto.setIndexHistory(
                getIndicesHistoryForCity(cityName, null, null)
        );

        return dto;
    }

    private City findCityByName(String cityName) {
        return cityRepository.findByNameIgnoreCase(cityName)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                        "Ville non trouvée: " + cityName));
    }

    private void validateAreaCode(City city, String cityName) {
        if (city.getAreaCode() == null) {
            throw new GlobalException.ResourceNotFoundException(
                    "Aucun areaCode pour la ville: " + cityName);
        }
    }

    private LocalDateTime[] getDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDateTime.now().minusDays(30);

        LocalDateTime end = (endDate != null)
                ? endDate.atTime(23, 59, 59)
                : LocalDateTime.now();

        return new LocalDateTime[]{start, end};
    }

    private Double findFirstNonNull(
            List<AirQualityMeasurement> measurements,
            Function<AirQualityMeasurement, Double> getter
    ) {
        return measurements.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<AirQualityDataResponseDto> getTopCitiesWithDataInDepartment(String departmentCode, int limit) {
        List<City> citiesWithData = cityRepository.findAll().stream()
                .filter(city -> city.getInseeCode() != null
                        && city.getInseeCode().startsWith(departmentCode))
                .filter(city -> {
                    boolean hasMeasurements = measurementRepository
                            .findTopByStation_City_InseeCodeOrderByMeasuredAtDesc(city.getInseeCode())
                            .isPresent();

                    if (!hasMeasurements && city.getAreaCode() != null) {
                        hasMeasurements = measurementRepository
                                .findTopByStation_City_AreaCodeOrderByMeasuredAtDesc(city.getAreaCode())
                                .isPresent();
                    }

                    return hasMeasurements;
                })
                .sorted((c1, c2) -> {
                    int pop1 = c1.getPopulation() != null ? c1.getPopulation() : 0;
                    int pop2 = c2.getPopulation() != null ? c2.getPopulation() : 0;
                    return Integer.compare(pop2, pop1);
                })
                .limit(limit)
                .toList();

        return citiesWithData.stream()
                .map(city -> getCompleteDataForCity(city.getName()))
                .toList();
    }
}