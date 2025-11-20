package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.ExportDto;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.AirQualityIndexRepository;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.WeatherRepository;
import com.airSphereConnect.services.ExportService;
import com.airSphereConnect.utils.CsvExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
            AirQualityIndexRepository airQualityIndexRepository,
            CsvExporter csvExporter) {
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

    /**
     * Export Weather uniquement
     */
    private List<ExportDto> getWeatherData(City city, LocalDate dateDebut, LocalDate dateFin) {
        List<WeatherMeasurement> weatherMeasurements = weatherRepository.findByCityId(city.getId()).stream()
            .filter(wm -> (dateDebut == null || !wm.getMeasuredAt().toLocalDate().isBefore(dateDebut)) &&
                          (dateFin == null || !wm.getMeasuredAt().toLocalDate().isAfter(dateFin)))
            .toList();

        return weatherMeasurements.stream()
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

    /**
     * Export Air Quality uniquement avec fallback sur la station la plus proche
     */
    private List<ExportDto> getAirQualityData(City city, LocalDate dateDebut, LocalDate dateFin) {
        List<ExportDto> airData = new ArrayList<>();

        List<AirQualityStation> stations;

        // Essayer d'abord avec les stations de la ville
        if (!city.getAirQualityStations().isEmpty()) {
            stations = city.getAirQualityStations();
        }
        // Utiliser la station la plus proche
        else {
            AirQualityStation nearestStation = findNearestStation(city);
            if (nearestStation != null) {
                log.info("✅ [EXPORT AIR] Ville {} sans station - Station la plus proche: {} ({})",
                        city.getName(), nearestStation.getName(), nearestStation.getCode());
                stations = List.of(nearestStation);
            } else {
                log.warn("⚠️ [EXPORT AIR] Aucune station trouvée pour la ville {}", city.getName());
                return airData;
            }
        }

        // 3️⃣ Récupérer les mesures des stations
        for (AirQualityStation station : stations) {
            List<AirQualityMeasurement> measurements = station.getMeasurements().stream()
                .filter(aqm -> (dateDebut == null || !aqm.getMeasuredAt().toLocalDate().isBefore(dateDebut)) &&
                              (dateFin == null || !aqm.getMeasuredAt().toLocalDate().isAfter(dateFin)))
                .toList();

            for (AirQualityMeasurement aqm : measurements) {
                LocalDate measureDate = aqm.getMeasuredAt().toLocalDate();

                var index = city.getAreaCode() != null
                    ? airQualityIndexRepository.findByAreaCodeAndMeasuredAt(
                        city.getAreaCode(),
                        measureDate.atStartOfDay()
                      ).orElse(null)
                    : null;

                ExportDto dto = new ExportDto(
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
                airData.add(dto);
            }
        }

        return airData;
    }

    /**
     * Export combiné avec fallback sur la station la plus proche
     */
    private List<ExportDto> getCombinedData(City city, LocalDate dateDebut, LocalDate dateFin) {
        List<WeatherMeasurement> weatherMeasurements = weatherRepository.findByCityId(city.getId()).stream()
                .filter(wm -> (dateDebut == null || !wm.getMeasuredAt().toLocalDate().isBefore(dateDebut)) &&
                        (dateFin == null || !wm.getMeasuredAt().toLocalDate().isAfter(dateFin)))
                .toList();

        List<ExportDto> mergedData = new ArrayList<>();

        List<AirQualityStation> stations;

        // Déterminer quelles stations utiliser
        if (!city.getAirQualityStations().isEmpty()) {
            stations = city.getAirQualityStations();
        } else {
            AirQualityStation nearestStation = findNearestStation(city);
            if (nearestStation != null) {
                log.info("✅ [EXPORT COMBINED] Ville {} - Station la plus proche: {}",
                        city.getName(), nearestStation.getCode());
                stations = List.of(nearestStation);
            } else {
                // Pas de station du tout : retourner seulement les données météo
                log.warn("⚠️ [EXPORT COMBINED] Ville {} sans données air quality", city.getName());
                return weatherMeasurements.stream()
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
        }

        for (AirQualityStation station : stations) {
            List<AirQualityMeasurement> airQualityMeasurements = station.getMeasurements();

            Map<LocalDate, AirQualityMeasurement> lastestMeasurementsByDate = airQualityMeasurements.stream()
                    .collect(Collectors.toMap(
                            aqm -> aqm.getMeasuredAt().toLocalDate(),
                            Function.identity(),
                            (existing, replacement) -> existing.getMeasuredAt().isAfter(replacement.getMeasuredAt()) ? existing : replacement
                    ));

            for (WeatherMeasurement wm : weatherMeasurements) {
                AirQualityMeasurement aqm = lastestMeasurementsByDate.get(wm.getMeasuredAt().toLocalDate());

                if (aqm == null) {
                    continue;
                }

                var index = city.getAreaCode() != null
                    ? airQualityIndexRepository.findByAreaCodeAndMeasuredAt(
                        city.getAreaCode(),
                        wm.getMeasuredAt().toLocalDate().atStartOfDay()
                      ).orElse(null)
                    : null;

                ExportDto dto = new ExportDto(
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
                        format(station.getId()),
                        formatDouble(aqm.getPm25()),
                        formatDouble(aqm.getPm10()),
                        formatDouble(aqm.getNo2()),
                        formatDouble(aqm.getO3()),
                        aqm.getUnit() != null ? aqm.getUnit() : "/",
                        format(index != null ? index.getQualityIndex() : null),
                        index != null ? index.getQualityLabel() : "/"
                );

                mergedData.add(dto);
            }
        }
        return mergedData;
    }

    /**
     * Trouve une station pour la ville selon la priorité:
     * 1. Même code INSEE
     * 2. Même areaCode (département)
     * 3. Aucune station trouvée
     */
    private AirQualityStation findNearestStation(City city) {
        // 1️⃣ Chercher une station avec le même code INSEE
        if (city.getInseeCode() != null) {
            List<AirQualityStation> sameInseeStations = airQualityStationRepository.findAll().stream()
                .filter(s -> city.getInseeCode().equals(s.getInseeCode()))
                .toList();

            if (!sameInseeStations.isEmpty()) {
                log.debug("✅ [EXPORT] Station trouvée pour ville {} avec même INSEE {}",
                    city.getName(), city.getInseeCode());
                return sameInseeStations.get(0);
            }
        }

        // 2️⃣ Chercher une station dans le même département (areaCode)
        if (city.getAreaCode() != null) {
            List<AirQualityStation> sameAreaStations = airQualityStationRepository.findAll().stream()
                .filter(s -> city.getAreaCode().equals(s.getAreaCode()))
                .toList();

            if (!sameAreaStations.isEmpty()) {
                log.debug("✅ [EXPORT] Station trouvée pour ville {} avec même areaCode {}",
                    city.getName(), city.getAreaCode());
                return sameAreaStations.get(0);
            }
        }

        // 3️⃣ Aucune station trouvée
        log.warn("⚠️ [EXPORT] Aucune station trouvée pour ville {} (INSEE: {}, Area: {})",
            city.getName(), city.getInseeCode(), city.getAreaCode());
        return null;
    }

    // Helper methods pour formater les valeurs
    private String format(Object value) {
        return value != null ? String.valueOf(value) : "/";
    }

    private String formatDouble(Double value) {
        return value != null ? String.format("%.2f", value) : "/";
    }
}
