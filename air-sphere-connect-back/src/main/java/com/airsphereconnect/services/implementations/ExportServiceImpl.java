package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.ExportDto;
import com.airsphereconnect.entities.AirQualityIndex;
import com.airsphereconnect.entities.AirQualityMeasurement;
import com.airsphereconnect.entities.AirQualityStation;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.WeatherMeasurement;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.repositories.AirQualityIndexRepository;
import com.airsphereconnect.repositories.AirQualityStationRepository;
import com.airsphereconnect.repositories.CityRepository;
import com.airsphereconnect.repositories.WeatherRepository;
import com.airsphereconnect.services.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExportServiceImpl implements ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportServiceImpl.class);

    private final AirQualityStationRepository airQualityStationRepository;
    private final CityRepository cityRepository;
    private final WeatherRepository weatherRepository;
    private final AirQualityIndexRepository airQualityIndexRepository;

    public ExportServiceImpl(
            AirQualityStationRepository airQualityStationRepository,
            CityRepository cityRepository,
            WeatherRepository weatherRepository,
            AirQualityIndexRepository airQualityIndexRepository) {
        this.airQualityStationRepository = airQualityStationRepository;
        this.cityRepository = cityRepository;
        this.weatherRepository = weatherRepository;
        this.airQualityIndexRepository = airQualityIndexRepository;
    }

    @Override
    public List<ExportDto> getCompleteDataByCity(String inseeCode, LocalDate dateDebut, LocalDate dateFin, String type) {
        City city = cityRepository.findByInseeCode(inseeCode)
            .orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                "City with INSEE code " + inseeCode + " not found"));

        return switch (type) {
            case "air-quality" -> getAirQualityData(city, dateDebut, dateFin);
            case "weather" -> getWeatherData(city, dateDebut, dateFin);
            default -> getCombinedData(city, dateDebut, dateFin);
        };
    }

    private List<ExportDto> getWeatherData(City city, LocalDate dateDebut, LocalDate dateFin) {
        List<WeatherMeasurement> measurements = weatherRepository.findByCityId(city.getId()).stream()
            .filter(wm -> isInDateRange(wm.getMeasuredAt(), dateDebut, dateFin))
            .toList();
        return buildWeatherOnlyList(city, measurements);
    }

    private List<ExportDto> getAirQualityData(City city, LocalDate dateDebut, LocalDate dateFin) {
        List<AirQualityStation> stations = resolveStations(city);
        if (stations.isEmpty()) {
            log.warn("⚠️ [EXPORT AIR] Aucune station trouvée pour la ville {}", city.getName());
            return new ArrayList<>();
        }

        List<ExportDto> airData = new ArrayList<>();
        for (AirQualityStation station : stations) {
            station.getMeasurements().stream()
                .filter(aqm -> isInDateRange(aqm.getMeasuredAt(), dateDebut, dateFin))
                .map(aqm -> buildAirQualityDto(city, station, aqm))
                .forEach(airData::add);
        }
        return airData;
    }

    private List<ExportDto> getCombinedData(City city, LocalDate dateDebut, LocalDate dateFin) {
        List<WeatherMeasurement> weatherMeasurements = weatherRepository.findByCityId(city.getId()).stream()
            .filter(wm -> isInDateRange(wm.getMeasuredAt(), dateDebut, dateFin))
            .toList();

        List<AirQualityStation> stations = resolveStations(city);
        if (stations.isEmpty()) {
            log.warn("⚠️ [EXPORT COMBINED] Ville {} sans données air quality", city.getName());
            return buildWeatherOnlyList(city, weatherMeasurements);
        }

        List<ExportDto> mergedData = new ArrayList<>();
        for (AirQualityStation station : stations) {
            mergedData.addAll(mergeStationWithWeather(city, station, weatherMeasurements));
        }
        return mergedData;
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private boolean isInDateRange(LocalDateTime measuredAt, LocalDate start, LocalDate end) {
        LocalDate date = measuredAt.toLocalDate();
        return (start == null || !date.isBefore(start)) && (end == null || !date.isAfter(end));
    }

    private List<AirQualityStation> resolveStations(City city) {
        if (!city.getAirQualityStations().isEmpty()) {
            return city.getAirQualityStations();
        }
        AirQualityStation nearest = findNearestStation(city);
        if (nearest != null) {
            log.info("Ville {} sans station directe - station la plus proche: {} ({})",
                city.getName(), nearest.getName(), nearest.getCode());
            return List.of(nearest);
        }
        return List.of();
    }

    private AirQualityIndex lookupQualityIndex(City city, LocalDate date) {
        if (city.getAreaCode() == null) return null;
        return airQualityIndexRepository
            .findByAreaCodeAndMeasuredAt(city.getAreaCode(), date.atStartOfDay())
            .orElse(null);
    }

    private ExportDto buildAirQualityDto(City city, AirQualityStation station, AirQualityMeasurement aqm) {
        LocalDate measureDate = aqm.getMeasuredAt().toLocalDate();
        AirQualityIndex index = lookupQualityIndex(city, measureDate);
        return new ExportDto(
            measureDate,
            city.getName(),
            formatDouble(city.getLatitude()),
            formatDouble(city.getLongitude()),
            "/", "/", "/", "/", "/", "/", "/",
            format(station.getId()),
            formatDouble(aqm.getPm25()),
            formatDouble(aqm.getPm10()),
            formatDouble(aqm.getNo2()),
            formatDouble(aqm.getO3()),
            aqm.getUnit() != null ? aqm.getUnit() : "/",
            format(index != null ? index.getQualityIndex() : null),
            index != null ? index.getQualityLabel() : "/"
        );
    }

    private ExportDto buildCombinedDto(City city, AirQualityStation station,
                                       WeatherMeasurement wm, AirQualityMeasurement aqm) {
        LocalDate date = wm.getMeasuredAt().toLocalDate();
        AirQualityIndex index = lookupQualityIndex(city, date);
        return new ExportDto(
            date,
            city.getName(),
            formatDouble(city.getLatitude()),
            formatDouble(city.getLongitude()),
            format(city.getPopulation()),
            formatDouble(wm.getTemperature()),
            formatDouble(wm.getHumidity()),
            formatDouble(wm.getPressure()),
            formatDouble(wm.getWindSpeed()),
            formatDouble(wm.getWindDirection()),
            wm.getMessage() != null ? wm.getMessage() : "/",
            format(station.getId()),
            formatDouble(aqm.getPm25()),
            formatDouble(aqm.getPm10()),
            formatDouble(aqm.getNo2()),
            formatDouble(aqm.getO3()),
            aqm.getUnit() != null ? aqm.getUnit() : "/",
            format(index != null ? index.getQualityIndex() : null),
            index != null ? index.getQualityLabel() : "/"
        );
    }

    private List<ExportDto> buildWeatherOnlyList(City city, List<WeatherMeasurement> measurements) {
        return measurements.stream()
            .map(wm -> new ExportDto(
                wm.getMeasuredAt().toLocalDate(),
                city.getName(),
                formatDouble(city.getLatitude()),
                formatDouble(city.getLongitude()),
                format(city.getPopulation()),
                formatDouble(wm.getTemperature()),
                formatDouble(wm.getHumidity()),
                formatDouble(wm.getPressure()),
                formatDouble(wm.getWindSpeed()),
                formatDouble(wm.getWindDirection()),
                wm.getMessage() != null ? wm.getMessage() : "/",
                "/", "/", "/", "/", "/", "/", "/", "/"
            ))
            .toList();
    }

    private List<ExportDto> mergeStationWithWeather(City city, AirQualityStation station,
                                                     List<WeatherMeasurement> weatherMeasurements) {
        Map<LocalDate, AirQualityMeasurement> latestByDate = station.getMeasurements().stream()
            .collect(Collectors.toMap(
                aqm -> aqm.getMeasuredAt().toLocalDate(),
                Function.identity(),
                (a, b) -> a.getMeasuredAt().isAfter(b.getMeasuredAt()) ? a : b
            ));

        return weatherMeasurements.stream()
            .filter(wm -> latestByDate.containsKey(wm.getMeasuredAt().toLocalDate()))
            .map(wm -> buildCombinedDto(city, station, wm,
                latestByDate.get(wm.getMeasuredAt().toLocalDate())))
            .toList();
    }

    /**
     * Trouve une station pour la ville selon la priorité:
     * 1. Même code INSEE
     * 2. Même areaCode (département)
     * 3. Aucune station trouvée
     */
    private AirQualityStation findNearestStation(City city) {
        if (city.getInseeCode() != null) {
            List<AirQualityStation> sameInsee = airQualityStationRepository.findAll().stream()
                .filter(s -> city.getInseeCode().equals(s.getInseeCode()))
                .toList();
            if (!sameInsee.isEmpty()) {
                log.debug("✅ [EXPORT] Station trouvée pour ville {} avec même INSEE {}",
                    city.getName(), city.getInseeCode());
                return sameInsee.get(0);
            }
        }

        if (city.getAreaCode() != null) {
            List<AirQualityStation> sameArea = airQualityStationRepository.findAll().stream()
                .filter(s -> city.getAreaCode().equals(s.getAreaCode()))
                .toList();
            if (!sameArea.isEmpty()) {
                log.debug("✅ [EXPORT] Station trouvée pour ville {} avec même areaCode {}",
                    city.getName(), city.getAreaCode());
                return sameArea.get(0);
            }
        }

        log.warn("⚠️ [EXPORT] Aucune station trouvée pour ville {} (INSEE: {}, Area: {})",
            city.getName(), city.getInseeCode(), city.getAreaCode());
        return null;
    }

    private String format(Object value) {
        return value != null ? String.valueOf(value) : "/";
    }

    private String formatDouble(Double value) {
        return value != null ? String.format("%.2f", value) : "/";
    }
}
