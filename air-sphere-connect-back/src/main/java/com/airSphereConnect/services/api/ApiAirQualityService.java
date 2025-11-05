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

        // ✅ Toujours sauvegarder à minuit pour cohérence avec l'historique
        LocalDateTime syncDateTime = LocalDate.now().atStartOfDay();

        int savedCount = 0;
        for (Map.Entry<String, List<AirQualityDailyMeasureResponseDto>> entry : groupedByStation.entrySet()) {
            try {
                savedCount += saveMeasurementForStation(entry.getValue(), syncDateTime);
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
            List<AirQualityDailyMeasureResponseDto> stationMeasures,
            LocalDateTime syncDateTime) {

        AirQualityDailyMeasureResponseDto dailyDto = stationMeasures.get(0);
        AirQualityStation station = getOrCreateStation(dailyDto);
        if (station == null) {
            log.warn("⚠️ [ATMO] Mesures ignorées pour station {} : station introuvable",
                    dailyDto.codeStation());
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

        LocalDateTime syncDateTime = LocalDateTime.now();

        int savedCount = 0;
        int alertCount = 0;

        for (AirQualityIndexMeasureResponseDto indexDto : indexDtos) {
            try {
                // Chercher l'index existant par areaCode
                Optional<AirQualityIndex> existingIndex = indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc(indexDto.areaCode());

                AirQualityIndex index;
                if (existingIndex.isPresent()) {
                    // Mettre à jour l'index existant
                    index = existingIndex.get();
                    index.setQualityIndex(Integer.valueOf(indexDto.qualityIndex()));
                    index.setQualityLabel(indexDto.qualityLabel());
                    index.setQualityColor(indexDto.qualityColor());
                    index.setSource(indexDto.source());
                    index.setAreaName(indexDto.areaName());
                    index.setMeasuredAt(syncDateTime);
                    log.debug("🔄 [ATMO] Mise à jour indice existant pour {}", indexDto.areaCode());
                } else {
                    // Créer un nouvel index
                    index = mapper.toEntity(indexDto);
                    index.setMeasuredAt(syncDateTime);
                    log.debug("🆕 [ATMO] Création nouvel indice pour {}", indexDto.areaCode());
                }

                String alertMessage = AirQualityAlertUtils.determineAlertMessageWithArea(
                        Integer.valueOf(indexDto.qualityIndex()),
                        indexDto.areaName()
                );

                if (alertMessage != null) {
                    index.setAlertMessage(alertMessage);
                    alertCount++;
                    index.setAlert(true);
                    log.warn("⚠️ [ATMO] Alerte qualité air : {}", alertMessage);
                } else {
                    index.setAlertMessage(null);
                    index.setAlert(false);
                }

                Optional<AirQualityIndex> existingOpt =
                        indexRepository.findByAreaCodeAndMeasuredAt(index.getAreaCode(), index.getMeasuredAt());


                if (existingOpt.isPresent()) {
                    AirQualityIndex existing = existingOpt.get();

                    // 🔄 Mise à jour des champs nécessaires
                    existing.setQualityIndex(index.getQualityIndex());
                    existing.setQualityLabel(index.getQualityLabel());
                    existing.setQualityColor(index.getQualityColor());
                    existing.setAlertMessage(index.getAlertMessage());
                    existing.setSource(index.getSource());

                    indexRepository.save(existing);
                    log.debug("♻️ [ATMO] Index mis à jour pour {}", existing.getAreaCode());
                } else {
                    indexRepository.save(index);
                    log.debug("🆕 [ATMO] Nouvel index ajouté pour {}", index.getAreaCode());
                }

                savedCount++;

            } catch (Exception e) {
                log.error("❌ [ATMO] Erreur sauvegarde indice : {}", e.getMessage());
            }
        }

        log.info("✅ [ATMO] {} indices sauvegardés", savedCount);
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
            String inseeCode = String.valueOf(dto.inseeCode());
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