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

        if (city.getAreaCode() == null) {
            throw new GlobalException.ResourceNotFoundException(
                    "Aucun areaCode pour la ville: " + cityName);
        }

        AirQualityMeasurement measurement = measurementRepository
                .findTopByAreaCodeOrderByMeasuredAtDesc(city.getAreaCode())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                        "Aucune mesure trouvée pour: " + cityName));

        return mapper.toDto(measurement);
    }

    @Override
    public AirQualityIndexResponseDto getLatestIndexQualityForCity(String cityName) {
        City city = findCityByName(cityName);
        validateAreaCode(city, cityName);

        AirQualityIndex index = indexRepository
                .findFirstByAreaCodeOrderByMeasuredAtDesc(
                        Integer.parseInt(city.getAreaCode()))
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
        validateAreaCode(city, cityName);

        LocalDateTime[] period = getDateRange(startDate, endDate);

        List<AirQualityMeasurement> measurements = measurementRepository
                .findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                        city.getAreaCode(), period[0], period[1]);

        return measurements.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<AirQualityIndexResponseDto> getIndicesHistoryForCity(
            String cityName,
            LocalDate startDate,
            LocalDate endDate) {

        City city = findCityByName(cityName);
        validateAreaCode(city, cityName);

        LocalDateTime[] period = getDateRange(startDate, endDate);

        List<AirQualityIndex> indices = indexRepository
                .findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                        Integer.parseInt(city.getAreaCode()), period[0], period[1]);

        return indices.stream()
                .map(index -> {
                    String alertMessage = determineAlertMessage(index.getQualityIndex());
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

        // 📊 Indice ATMO
        if (city.getAreaCode() != null) {
            try {
                indexRepository
                        .findFirstByAreaCodeOrderByMeasuredAtDesc(
                                Integer.parseInt(city.getAreaCode()))
                        .ifPresent(index -> {
                            dto.setQualityIndex(index.getQualityIndex());
                            dto.setQualityLabel(index.getQualityLabel());
                            dto.setQualityColor(index.getQualityColor());
                            dto.setIndexMeasuredAt(index.getMeasuredAt());
                            dto.setAlertMessage(determineAlertMessage(index.getQualityIndex()));
                        });
            } catch (NumberFormatException e) {
                log.error("Code EPCI invalide pour la ville : ", city.getName());
            }
        }

        // 🌬️ Mesures de polluants
        if (city.getAreaCode() != null) {
            List<AirQualityMeasurement> measurements =
                    measurementRepository.findByAreaCodeOrderByMeasuredAtDesc(city.getAreaCode());

            if (!measurements.isEmpty()) {
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
            }
        }

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

    private String determineAlertMessage(Integer atmoIndex) {
        if (atmoIndex == null) return null;

        return switch (atmoIndex) {
            case 1, 2 -> null;
            case 3, 4 -> "Qualité de l'air moyenne - Personnes sensibles : limitez les activités intenses";
            case 5 -> "⚠️ Qualité de l'air dégradée - Évitez les efforts prolongés";
            case 6 -> "🚨 Qualité de l'air très mauvaise - Limitez toute activité physique";
            default -> null;
        };
    }
}