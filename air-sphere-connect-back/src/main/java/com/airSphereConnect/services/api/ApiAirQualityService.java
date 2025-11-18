package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.api.AirQualityDailyMeasureResponseDto;
import com.airSphereConnect.dtos.response.api.AirQualityIndexMeasureResponseDto;
import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.mapper.api.ApiAirQualityMapper;
import com.airSphereConnect.repositories.AirQualityIndexRepository;
import com.airSphereConnect.repositories.AirQualityMeasurementRepository;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.utils.AirQualityAlertUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ApiAirQualityService implements DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(ApiAirQualityService.class);

    private final WebClient atmoApiWebClient;
    private final AirQualityStationRepository stationRepository;
    private final AirQualityMeasurementRepository measurementRepository;
    private final AirQualityIndexRepository indexRepository;
    private final CityRepository cityRepository;
    private final ApiAirQualityMapper mapper;
    private final ObjectMapper objectMapper;

    @Value("${app.api.atmo.enabled:true}")
    private boolean enabled;

    @Value("${app.api.atmo.sync-interval-hours:12}")
    private int syncIntervalHours;

    private LocalDateTime lastSync;
    private int consecutiveErrors = 0;

    // URLs des services GeoJSON ATMO Occitanie
    private static final String QUALITY_INDEX =
            "/Indice_quotidien_de_qualité_de_l’air_pour_les_collectivités_territoriales_en_Occitanie/FeatureServer/0/query?where=1=1&outFields=*&outSR=4326&f=json";
    private static final String MEASURES_DAILY =
            "/mesures_occitanie_journaliere_poll_princ/FeatureServer/0/query?where=1=1&outFields=*&f=geojson";

    // Seuil d'alerte indice qualité
    private static final int ALERT_THRESHOLD = 3;
    // Timeout requête HTTP
    private static final int HTTP_TIMEOUT_SECONDS = 30;


    public ApiAirQualityService(WebClient atmoApiWebClient,
                                AirQualityStationRepository stationRepository,
                                AirQualityMeasurementRepository measurementRepository,
                                AirQualityIndexRepository indexRepository,
                                CityRepository cityRepository,
                                ApiAirQualityMapper mapper,
                                ObjectMapper objectMapper) {
        this.atmoApiWebClient = atmoApiWebClient;
        this.stationRepository = stationRepository;
        this.measurementRepository = measurementRepository;
        this.indexRepository = indexRepository;
        this.cityRepository = cityRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getServiceName() {
        return "AIR_QUALITY";
    }

    @Override
    public void syncData() {
        log.info("🔄 Début synchronisation ATMO Occitanie...");
        try {
            int measuresSynced = syncStationMeasures();
            int indexesSynced = syncQualityIndex();

            lastSync = LocalDateTime.now();
            consecutiveErrors = 0;

            log.info("✅ [ATMO] Sync terminée : {} mesures, {} indices",
                    measuresSynced, indexesSynced);

        } catch (Exception e) {
            consecutiveErrors++;
            log.error("❌ [ATMO] Erreur sync (tentative {}/3) : {}",
                    consecutiveErrors, e.getMessage(), e);
        }

        if (consecutiveErrors >= 3) {
            log.error("🚨 [ATMO] Service désactivé après 3 échecs consécutifs");
        }

    }

    private int syncStationMeasures() {
        log.info("📊 [ATMO] Récupération mesures stations");

        String json = fetchJson(MEASURES_DAILY);
        if (json == null) {
            log.warn("⚠️ [ATMO] Impossible de récupérer les mesures");
            return 0;
        }

        List<AirQualityDailyMeasureResponseDto> measureDtos =
                parseAtmoJson(json, "properties", AirQualityDailyMeasureResponseDto.class);
        log.info("📥 [ATMO] {} mesures parsées", measureDtos.size());

        Map<String, List<AirQualityDailyMeasureResponseDto>> groupedByStation =
                groupMeasuresByStation(measureDtos);

        int savedCount = 0;
        for (Map.Entry<String, List<AirQualityDailyMeasureResponseDto>> entry : groupedByStation.entrySet()) {
            try {
                savedCount += saveMeasurementForStation(entry.getValue());
            } catch (Exception e) {
                log.error("❌ [ATMO] Erreur sauvegarde mesure station {} : {}",
                        entry.getKey(), e.getMessage());
            }
        }

        log.info("✅ [ATMO] {} mesures sauvegardées sur {} stations",
                savedCount, groupedByStation.size());
        return savedCount;
    }

    private int saveMeasurementForStation(
            List<AirQualityDailyMeasureResponseDto> stationMeasures) {

        AirQualityDailyMeasureResponseDto dailyDto = stationMeasures.get(0);
        AirQualityStation station = getOrCreateStation(dailyDto);
        if (station == null) {
            log.warn("⚠️ [ATMO] Mesures ignorées pour station {} : station introuvable",
                    dailyDto.codeStation());
            return 0;
        }

        // ✅ CORRECTION : Extraire la vraie date de l'API au lieu d'utiliser LocalDate.now()
        LocalDateTime syncDateTime = timestampToLocalDateTime(dailyDto.dateDebutTimestamp());
        log.debug("📅 [ATMO] Station {} - Date extraite de l'API : {}",
                dailyDto.codeStation(), syncDateTime);

        // ✅ Vérifier si une mesure existe déjà pour cette station et cette date
        boolean exists = measurementRepository.existsByStationAndMeasuredAt(station, syncDateTime);
        if (exists) {
            log.debug("⏭️ [ATMO] Mesure déjà existante pour station {} à la date {}",
                    dailyDto.codeStation(), syncDateTime);
            return 0;
        }

        AirQualityMeasurement measurement = new AirQualityMeasurement();
        measurement.setStation(station);
        measurement.setMeasuredAt(syncDateTime);
        measurement.setUnit(dailyDto.polluantUnit() != null ? dailyDto.polluantUnit() : "µg/m³");


        for (AirQualityDailyMeasureResponseDto dto : stationMeasures) {
            fillPollutant(measurement, dto.polluantName(), dto.polluantValue());
        }

        measurementRepository.save(measurement);
        return 1;
    }

    private int syncQualityIndex() {
        log.info("📊 [ATMO] Récupération indices qualité");

        String json = fetchJson(QUALITY_INDEX);
        if (json == null) {
            log.warn("⚠️ [ATMO] Impossible de récupérer les indices");
            return 0;
        }

        List<AirQualityIndexMeasureResponseDto> indexDtos = parseAtmoJson(json, "attributes", AirQualityIndexMeasureResponseDto.class);
        log.info("📥 [ATMO] {} indices parsés", indexDtos.size());

        int savedCount = 0;
        int skippedCount = 0;
        int alertCount = 0;

        for (AirQualityIndexMeasureResponseDto indexDto : indexDtos) {
            try {
                // ✅ CORRECTION : Extraire la vraie date de l'API au lieu d'utiliser LocalDate.now()
                LocalDateTime syncDateTime = timestampToLocalDateTime(indexDto.dateEchTimestamp());
                log.debug("📅 [ATMO] Zone {} - Date extraite de l'API : {}",
                        indexDto.areaCode(), syncDateTime);

                // ✅ Vérifier si un indice existe déjà pour cette zone ET cette date
                Optional<AirQualityIndex> existingOpt =
                        indexRepository.findByAreaCodeAndMeasuredAt(indexDto.areaCode(), syncDateTime);

                if (existingOpt.isPresent()) {
                    // ✅ Un indice existe déjà pour aujourd'hui : mise à jour des valeurs
                    AirQualityIndex existing = existingOpt.get();

                    existing.setQualityIndex(Integer.valueOf(indexDto.qualityIndex()));
                    existing.setQualityLabel(indexDto.qualityLabel());
                    existing.setQualityColor(indexDto.qualityColor());
                    existing.setSource(indexDto.source());
                    existing.setAreaName(indexDto.areaName());

                    String alertMessage = AirQualityAlertUtils.determineAlertMessageWithArea(
                            Integer.valueOf(indexDto.qualityIndex()),
                            indexDto.areaName()
                    );

                    if (alertMessage != null) {
                        existing.setAlertMessage(alertMessage);
                        existing.setAlert(true);
                        alertCount++;
                        log.warn("⚠️ [ATMO] Alerte qualité air : {}", alertMessage);
                    } else {
                        existing.setAlertMessage(null);
                        existing.setAlert(false);
                    }

                    indexRepository.save(existing);
                    log.debug("♻️ [ATMO] Index mis à jour pour {} ({})", existing.getAreaCode(), syncDateTime.toLocalDate());
                    savedCount++;
                } else {
                    // ✅ Aucun indice pour aujourd'hui : créer un nouveau
                    AirQualityIndex newIndex = mapper.toEntity(indexDto);
                    newIndex.setMeasuredAt(syncDateTime);

                    String alertMessage = AirQualityAlertUtils.determineAlertMessageWithArea(
                            Integer.valueOf(indexDto.qualityIndex()),
                            indexDto.areaName()
                    );

                    if (alertMessage != null) {
                        newIndex.setAlertMessage(alertMessage);
                        newIndex.setAlert(true);
                        alertCount++;
                        log.warn("⚠️ [ATMO] Alerte qualité air : {}", alertMessage);
                    } else {
                        newIndex.setAlertMessage(null);
                        newIndex.setAlert(false);
                    }

                    indexRepository.save(newIndex);
                    log.debug("🆕 [ATMO] Nouvel index créé pour {} ({})", newIndex.getAreaCode(), syncDateTime.toLocalDate());
                    savedCount++;
                }

            } catch (Exception e) {
                log.error("❌ [ATMO] Erreur sauvegarde indice : {}", e.getMessage());
            }
        }

        log.info("✅ [ATMO] {} indices sauvegardés, {} déjà à jour", savedCount, skippedCount);
        if (alertCount > 0) {
            log.warn("⚠️ [ATMO] {} alertes détectées", alertCount);
        }
        return savedCount;
    }

    private <T> List<T> parseAtmoJson(String json, String dataField, Class<T> dtoClass) {
        List<T> results = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode featuresNode = rootNode.get("features");

            if (featuresNode == null || !featuresNode.isArray()) {
                log.warn("⚠️ [ATMO] Structure JSON invalide");
                return results;
            }

            for (JsonNode featureNode : featuresNode) {
                JsonNode dataNode = featureNode.get(dataField);

                if (dataNode != null) {
                    T dto = objectMapper.treeToValue(dataNode, dtoClass);
                    results.add(dto);
                }
            }

            log.debug("✅ [ATMO] {} éléments parsés depuis {}", results.size(), dataField);

        } catch (Exception e) {
            log.error("❌ [ATMO] Erreur parsing JSON : {}", e.getMessage());
        }

        return results;
    }

    private Map<String, List<AirQualityDailyMeasureResponseDto>> groupMeasuresByStation(
            List<AirQualityDailyMeasureResponseDto> measures) {

        Map<String, List<AirQualityDailyMeasureResponseDto>> grouped = new HashMap<>();

        for (AirQualityDailyMeasureResponseDto dto : measures) {
            String stationCode = dto.codeStation();
            grouped.computeIfAbsent(stationCode, key -> new ArrayList<>()).add(dto);
        }

        log.debug("🔀 [ATMO] {} stations groupées", grouped.size());
        return grouped;
    }

    private AirQualityStation getOrCreateStation(AirQualityDailyMeasureResponseDto dto) {
        Optional<AirQualityStation> existingStation = stationRepository.findByCode(dto.codeStation());

        if (existingStation.isPresent()) {
            return existingStation.get();
        }

        log.debug("🆕 [ATMO] Création station {}", dto.codeStation());
        AirQualityStation newStation = mapper.toEntity(dto);

        // ✅ CORRECTION : Chercher la ville AVANT de sauvegarder
        if (dto.inseeCode() != null) {
            // ✅ FIX: Padder le code INSEE pour avoir toujours 5 chiffres
            // L'API ATMO retourne 9261 mais en base on a "09261"
            String inseeCode = String.format("%05d", dto.inseeCode());
            newStation.setInseeCode(inseeCode);

            cityRepository.findByInseeCode(inseeCode)
                    .ifPresentOrElse(
                            city -> {
                                newStation.setCity(city);
                                newStation.setAreaCode(city.getAreaCode());
                                log.debug("🔗 [ATMO] Station {} liée à {}", dto.codeStation(), city.getName());
                            },
                            () -> {
                                log.warn("""
                    ⚠️ [ATMO] Ville introuvable pour INSEE {} (station {}).
                    Vérifions si elle existe réellement dans la base :
                    """, inseeCode, dto.codeStation());

                                boolean existsInDb = cityRepository.existsByInseeCode(inseeCode);
                                if (existsInDb) {
                                    log.error("🚨 [ATMO] Incohérence détectée : la ville avec code INSEE {} existe en DB, mais la recherche JPA ne l’a pas trouvée !", inseeCode);
                                } else {
                                    log.warn("⚠️ [ATMO] Code INSEE {} inexistant en base : probablement une erreur de l’API ATMO ou une zone non communale.", inseeCode);
                                }
                            }
                    );
        }

        // ❌ Si pas de ville trouvée, on ne sauvegarde PAS
        if (newStation.getCity() == null) {
            log.warn("⚠️ [ATMO] Station {} ignorée : pas de ville associée", dto.codeStation());
            return null;
        }

        return stationRepository.save(newStation);
    }


    private String fetchJson(String uri) {
        try {
            log.debug("📥 [ATMO] Fetch {}", uri);

            return atmoApiWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(HTTP_TIMEOUT_SECONDS))
                    .block();

        } catch (Exception e) {
            log.error("❌ [ATMO] Erreur fetch {} : {}", uri, e.getMessage());
            return null;
        }
    }

    public void fillPollutant(AirQualityMeasurement measurement,
                              String pollutantName,
                              Double value) {
        if (pollutantName == null || value == null) return;

        switch (pollutantName.toUpperCase()) {
            case "PM10" -> measurement.setPm10(value);
            case "PM2.5", "PM25" -> measurement.setPm25(value);
            case "NO2" -> measurement.setNo2(value);
            case "O3" -> measurement.setO3(value);
            case "SO2" -> measurement.setSo2(value);
            default -> log.debug("⚠️ [ATMO] Polluant inconnu : {}", pollutantName);

        }
    }

    /**
     * 📅 Convertir un timestamp ATMO (millisecondes) en LocalDateTime
     * L'API ATMO retourne des timestamps UTC, on les convertit en LocalDateTime à minuit
     */
    private LocalDateTime timestampToLocalDateTime(Long timestampMs) {
        if (timestampMs == null) {
            log.warn("⚠️ [ATMO] Timestamp null, utilisation de la date du jour par défaut");
            return LocalDate.now().atStartOfDay();
        }

        // Convertir le timestamp en Instant (UTC), puis en date locale (jour uniquement)
        java.time.Instant instant = java.time.Instant.ofEpochMilli(timestampMs);
        java.time.LocalDate date = instant.atZone(java.time.ZoneOffset.UTC).toLocalDate();

        // Retourner le LocalDateTime à minuit du jour concerné
        return date.atStartOfDay();
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
}