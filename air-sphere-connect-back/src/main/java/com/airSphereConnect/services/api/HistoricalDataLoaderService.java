package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.AirQualityHourlyMeasureResponseDto;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.repositories.AirQualityMeasurementRepository;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ⚡ Service ponctuel pour charger l'historique des 30 derniers jours
 * À utiliser UNE SEULE FOIS pour initialiser les données historiques
 */
@Service
public class HistoricalDataLoaderService {

    private static final Logger log = LoggerFactory.getLogger(HistoricalDataLoaderService.class);

    private final WebClient atmoApiWebClient;
    private final AirQualityStationRepository stationRepository;
    private final AirQualityMeasurementRepository measurementRepository;
    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;

    // URL du service horaire 30 jours
    private static final String MEASURES_HOURLY_30D =
            "/Mesure_horaire_(30j)_Region_Occitanie_Polluants_Reglementaires_1/FeatureServer/0/query?where=1=1&outFields=*&f=json";

    public HistoricalDataLoaderService(
            WebClient atmoApiWebClient,
            AirQualityStationRepository stationRepository,
            AirQualityMeasurementRepository measurementRepository,
            CityRepository cityRepository,
            ObjectMapper objectMapper) {
        this.atmoApiWebClient = atmoApiWebClient;
        this.stationRepository = stationRepository;
        this.measurementRepository = measurementRepository;
        this.cityRepository = cityRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 📊 Charger l'historique complet des 30 derniers jours
     */
    public void loadLast30DaysHistory() {
        log.info("🚀 [HISTORIQUE] Début du chargement des 30 derniers jours...");

        // 📄 Pagination : l'API ATMO limite à 1000-2000 résultats par requête
        List<AirQualityHourlyMeasureResponseDto> hourlyData = fetchAllPaginatedData();

        if (hourlyData.isEmpty()) {
            log.error("❌ [HISTORIQUE] Aucune donnée récupérée");
            return;
        }

        log.info("📥 [HISTORIQUE] {} mesures horaires récupérées au total", hourlyData.size());

        // 🔄 Grouper par station + jour (PAS par polluant !)
        Map<String, List<AirQualityHourlyMeasureResponseDto>> groupedByDayStation =
                groupByDayStation(hourlyData);

        log.info("📊 [HISTORIQUE] {} jours/station uniques à sauvegarder",
                groupedByDayStation.size());

        int savedCount = 0;
        int skippedCount = 0;

        for (Map.Entry<String, List<AirQualityHourlyMeasureResponseDto>> entry : groupedByDayStation.entrySet()) {
            try {
                boolean saved = saveDailyMeasurementAllPollutants(entry.getValue());
                if (saved) {
                    savedCount++;
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                log.error("❌ [HISTORIQUE] Erreur sauvegarde : {}", e.getMessage());
                skippedCount++;
            }
        }

        log.info("✅ [HISTORIQUE] Terminé : {} mesures sauvegardées, {} ignorées",
                savedCount, skippedCount);
    }

    /**
     * 🔄 Grouper les mesures horaires par : jour + station (SANS polluant)
     * Cela permet de créer UNE SEULE ligne avec TOUS les polluants
     */
    private Map<String, List<AirQualityHourlyMeasureResponseDto>> groupByDayStation(
            List<AirQualityHourlyMeasureResponseDto> hourlyData) {

        Map<String, List<AirQualityHourlyMeasureResponseDto>> grouped = new HashMap<>();

        for (AirQualityHourlyMeasureResponseDto dto : hourlyData) {
            LocalDateTime measuredAt = dto.getMeasuredAt();
            if (measuredAt == null) continue;

            LocalDate day = measuredAt.toLocalDate();
            // ✅ Clé sans polluant : station + jour uniquement
            String key = String.format("%s_%s",
                    dto.codeStation(),
                    day.toString());

            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(dto);
        }

        return grouped;
    }

    /**
     * 💾 Sauvegarder UNE SEULE ligne avec TOUS les polluants
     * (au lieu d'une ligne par polluant)
     */
    private boolean saveDailyMeasurementAllPollutants(List<AirQualityHourlyMeasureResponseDto> hourlyMeasures) {
        if (hourlyMeasures.isEmpty()) return false;

        AirQualityHourlyMeasureResponseDto firstDto = hourlyMeasures.get(0);

        // Récupérer ou créer la station
        AirQualityStation station = getOrCreateStation(firstDto);
        if (station == null) return false;

        // Date de la mesure (minuit du jour concerné)
        LocalDate day = firstDto.getMeasuredAt().toLocalDate();
        LocalDateTime measuredAt = day.atStartOfDay();

        // Vérifier si cette mesure existe déjà
        boolean exists = measurementRepository.existsByStationAndMeasuredAt(station, measuredAt);
        if (exists) {
            return false; // Skip si déjà présent
        }

        // Créer la mesure
        AirQualityMeasurement measurement = new AirQualityMeasurement();
        measurement.setStation(station);
        measurement.setMeasuredAt(measuredAt);
        measurement.setUnit("µg/m³");

        // 📊 Grouper par polluant et calculer les moyennes
        Map<String, List<Double>> pollutantValues = new HashMap<>();

        for (AirQualityHourlyMeasureResponseDto dto : hourlyMeasures) {
            if (dto.polluantName() != null && dto.polluantValue() != null) {
                pollutantValues
                    .computeIfAbsent(dto.polluantName().toUpperCase(), k -> new ArrayList<>())
                    .add(dto.polluantValue());
            }
        }

        // Calculer et assigner les moyennes arrondies pour chaque polluant
        boolean hasData = false;
        for (Map.Entry<String, List<Double>> entry : pollutantValues.entrySet()) {
            double average = entry.getValue().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

            // ✅ Arrondir à 2 décimales
            double rounded = Math.round(average * 100.0) / 100.0;

            fillPollutant(measurement, entry.getKey(), rounded);
            hasData = true;
        }

        // Ne pas sauvegarder si aucun polluant
        if (!hasData) {
            log.debug("⏭️ [HISTORIQUE] Station {} jour {} : aucun polluant, ignoré",
                station.getCode(), day);
            return false;
        }

        measurementRepository.save(measurement);
        return true;
    }

    private AirQualityStation getOrCreateStation(AirQualityHourlyMeasureResponseDto dto) {
        Optional<AirQualityStation> existingStation = stationRepository.findByCode(dto.codeStation());

        if (existingStation.isPresent()) {
            return existingStation.get();
        }

        log.debug("🆕 [HISTORIQUE] Création station {}", dto.codeStation());

        // Créer manuellement la station (pas de mapper pour ce DTO)
        AirQualityStation newStation = new AirQualityStation();
        newStation.setCode(dto.codeStation());
        newStation.setName(dto.nomStation());

        // Lier à la ville si possible
        if (dto.inseeCode() != null) {
            String inseeCode = String.valueOf(dto.inseeCode());
            newStation.setInseeCode(inseeCode);

            cityRepository.findByInseeCode(inseeCode).ifPresent(city -> {
                newStation.setCity(city);
                newStation.setAreaCode(city.getAreaCode());
                log.debug("🔗 [HISTORIQUE] Station {} liée à {}", dto.codeStation(), city.getName());
            });
        }

        if (newStation.getCity() == null) {
            log.warn("⚠️ [HISTORIQUE] Station {} ignorée : pas de ville associée", dto.codeStation());
            return null;
        }

        return stationRepository.save(newStation);
    }

    private void fillPollutant(AirQualityMeasurement measurement, String pollutantName, Double value) {
        if (pollutantName == null || value == null) return;

        switch (pollutantName.toUpperCase()) {
            case "PM10" -> measurement.setPm10(value);
            case "PM2.5", "PM25" -> measurement.setPm25(value);
            case "NO2" -> measurement.setNo2(value);
            case "O3" -> measurement.setO3(value);
            case "SO2" -> measurement.setSo2(value);
            default -> log.debug("⚠️ [HISTORIQUE] Polluant inconnu : {}", pollutantName);
        }
    }

    /**
     * 📄 Récupérer toutes les données avec pagination
     * L'API ArcGIS limite les résultats à 1000-2000 par requête
     */
    private List<AirQualityHourlyMeasureResponseDto> fetchAllPaginatedData() {
        List<AirQualityHourlyMeasureResponseDto> allData = new ArrayList<>();
        int offset = 0;
        int pageSize = 1000; // Limite de l'API ATMO ArcGIS
        int totalFetched = 0;

        while (true) {
            String paginatedUrl = MEASURES_HOURLY_30D + "&resultOffset=" + offset + "&resultRecordCount=" + pageSize;
            log.info("📥 [HISTORIQUE] Requête page offset={}", offset);

            String json = fetchJson(paginatedUrl);
            if (json == null) {
                log.warn("⚠️ [HISTORIQUE] Arrêt pagination : erreur fetch à offset {}", offset);
                break;
            }

            List<AirQualityHourlyMeasureResponseDto> pageData =
                    parseAtmoJson(json, "attributes", AirQualityHourlyMeasureResponseDto.class);

            // Si aucune donnée, on a atteint la fin
            if (pageData.isEmpty()) {
                log.info("✅ [HISTORIQUE] Fin de pagination : aucune donnée à offset {}", offset);
                break;
            }

            allData.addAll(pageData);
            totalFetched += pageData.size();
            log.info("📊 [HISTORIQUE] Page chargée : {} mesures (total: {})", pageData.size(), totalFetched);

            // Continuer tant qu'on obtient des résultats
            // On s'arrête seulement si on obtient 0 résultats
            offset += pageSize;

            // Sécurité : limiter à 150 000 résultats max (suffisant pour 30j)
            if (totalFetched >= 150000) {
                log.warn("⚠️ [HISTORIQUE] Limite de sécurité atteinte (150k mesures)");
                break;
            }
        }

        log.info("✅ [HISTORIQUE] Pagination terminée : {} mesures au total", totalFetched);
        return allData;
    }

    private String fetchJson(String uri) {
        try {
            return atmoApiWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60)) // Plus long car beaucoup de données
                    .block();

        } catch (Exception e) {
            log.error("❌ [HISTORIQUE] Erreur fetch {} : {}", uri, e.getMessage());
            return null;
        }
    }

    private <T> List<T> parseAtmoJson(String json, String dataField, Class<T> dtoClass) {
        List<T> results = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode featuresNode = rootNode.get("features");

            if (featuresNode == null || !featuresNode.isArray()) {
                log.warn("⚠️ [HISTORIQUE] Structure JSON invalide");
                return results;
            }

            for (JsonNode featureNode : featuresNode) {
                JsonNode dataNode = featureNode.get(dataField);

                if (dataNode != null) {
                    T dto = objectMapper.treeToValue(dataNode, dtoClass);
                    results.add(dto);
                }
            }

            log.debug("✅ [HISTORIQUE] {} éléments parsés depuis {}", results.size(), dataField);

        } catch (Exception e) {
            log.error("❌ [HISTORIQUE] Erreur parsing JSON : {}", e.getMessage());
        }

        return results;
    }
}
