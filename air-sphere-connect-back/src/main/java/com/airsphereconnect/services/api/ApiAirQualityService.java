package com.airsphereconnect.services.api;

import com.airsphereconnect.dtos.ExternalAlertDto;
import com.airsphereconnect.dtos.response.AirQualityDailyMeasureResponseDto;
import com.airsphereconnect.dtos.response.AirQualityIndexMeasureResponseDto;
import com.airsphereconnect.entities.AirQualityIndex;
import com.airsphereconnect.entities.AirQualityMeasurement;
import com.airsphereconnect.entities.AirQualityStation;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.mapper.api.ApiAirQualityMapper;
import com.airsphereconnect.repositories.AirQualityIndexRepository;
import com.airsphereconnect.repositories.AirQualityMeasurementRepository;
import com.airsphereconnect.repositories.AirQualityStationRepository;
import com.airsphereconnect.repositories.CityRepository;
import com.airsphereconnect.services.ExternalAlertProcessingService;
import com.airsphereconnect.utils.AirQualityAlertUtils;
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

/**
 * Service de synchronisation des données de qualité de l'air depuis l'API ATMO Occitanie.
 * Gère la récupération, le parsing et la sauvegarde des mesures de polluants et des indices de qualité de l'air.
 */
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
    private final ExternalAlertProcessingService externalAlertProcessingService;

    @Value("${app.api.atmo.enabled:true}")
    private boolean enabled;

    @Value("${app.api.atmo.sync-interval-hours:12}")
    private int syncIntervalHours;

    private LocalDateTime lastSync;
    private int consecutiveErrors = 0;

    /** URL du service d'indices de qualité de l'air ATMO Occitanie */
    private static final String QUALITY_INDEX =
            "/Indice_quotidien_de_qualité_de_l'air_pour_les_collectivités_territoriales_en_Occitanie/FeatureServer/0/query?where=1=1&outFields=*&outSR=4326&f=json";

    /** URL du service de mesures journalières des polluants ATMO Occitanie */
    private static final String MEASURES_DAILY =
            "/mesures_occitanie_journaliere_poll_princ/FeatureServer/0/query?where=1=1&outFields=*&f=geojson";

    /** Seuil d'alerte pour l'indice de qualité de l'air */
    private static final int ALERT_THRESHOLD = 3;

    /** Timeout des requêtes HTTP en secondes */
    private static final int HTTP_TIMEOUT_SECONDS = 30;

    /**
     * Constructeur du service de synchronisation ATMO.
     *
     * @param atmoApiWebClient          Client WebFlux pour les appels API ATMO
     * @param stationRepository         Repository des stations de mesure
     * @param measurementRepository     Repository des mesures de qualité de l'air
     * @param indexRepository           Repository des indices de qualité de l'air
     * @param cityRepository            Repository des villes
     * @param mapper                    Mapper pour les DTOs ATMO
     * @param objectMapper              Mapper JSON
     */
    public ApiAirQualityService(WebClient atmoApiWebClient,
                                AirQualityStationRepository stationRepository,
                                AirQualityMeasurementRepository measurementRepository,
                                AirQualityIndexRepository indexRepository,
                                CityRepository cityRepository,
                                ApiAirQualityMapper mapper,
                                ObjectMapper objectMapper,
                                ExternalAlertProcessingService externalAlertProcessingService) {
        this.atmoApiWebClient = atmoApiWebClient;
        this.stationRepository = stationRepository;
        this.measurementRepository = measurementRepository;
        this.indexRepository = indexRepository;
        this.cityRepository = cityRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.externalAlertProcessingService = externalAlertProcessingService;
    }

    /**
     * Retourne le nom du service de synchronisation.
     *
     * @return Le nom du service
     */
    @Override
    public String getServiceName() {
        return "AIR_QUALITY";
    }

    /**
     * Synchronise les données de qualité de l'air depuis l'API ATMO Occitanie.
     * Récupère les mesures des stations et les indices de qualité de l'air.
     */
    @Override
    public void syncData() {
        log.info("🔄 Début synchronisation ATMO Occitanie...");
        try {
            int measuresSynced = syncStationMeasures();
            int indexesSynced = syncQualityIndex();
            checkAndProcessAirQualityAlerts();

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

    /**
     * Synchronise les mesures des stations de qualité de l'air.
     * Récupère les données depuis l'API, les parse et les sauvegarde en base de données.
     *
     * @return Le nombre de mesures synchronisées
     */
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

    /**
     * Sauvegarde les mesures de qualité de l'air pour une station donnée.
     * Évite les doublons en vérifiant si une mesure existe déjà pour la date du jour.
     *
     * @param stationMeasures La liste des mesures pour la station
     * @return Le nombre de mesures sauvegardées (0 ou 1)
     */
    private int saveMeasurementForStation(
            List<AirQualityDailyMeasureResponseDto> stationMeasures) {

        AirQualityDailyMeasureResponseDto dailyDto = stationMeasures.get(0);
        AirQualityStation station = getOrCreateStation(dailyDto);
        if (station == null) {
            log.warn("⚠️ [ATMO] Mesures ignorées pour station {} : station introuvable",
                    dailyDto.codeStation());
            return 0;
        }

        // ✅ Utiliser la date du jour de la synchronisation (comme pour Weather)
        // Cela évite les duplicata quand ATMO retourne toujours le même timestamp
        LocalDateTime syncDateTime = LocalDate.now().atStartOfDay();
        log.debug("📅 [ATMO] Station {} - Date de synchronisation : {}",
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

    /**
     * Synchronise les indices de qualité de l'air pour les zones géographiques.
     * Crée ou met à jour les indices et détecte les alertes de qualité de l'air.
     *
     * @return Le nombre d'indices synchronisés
     */
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
                // ✅ Utiliser la date du jour de la synchronisation (comme pour Weather)
                // Cela évite les duplicata quand ATMO retourne toujours le même timestamp
                LocalDateTime syncDateTime = LocalDate.now().atStartOfDay();
                log.debug("📅 [ATMO] Zone {} - Date de synchronisation : {}",
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
                    alertCount += applyAlertToIndex(existing, indexDto);
                    indexRepository.save(existing);
                    log.debug("♻️ [ATMO] Index mis à jour pour {} ({})", existing.getAreaCode(), syncDateTime.toLocalDate());
                    savedCount++;
                } else {
                    // ✅ Aucun indice pour aujourd'hui : créer un nouveau
                    AirQualityIndex newIndex = mapper.toEntity(indexDto);
                    newIndex.setMeasuredAt(syncDateTime);
                    alertCount += applyAlertToIndex(newIndex, indexDto);
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

    private int applyAlertToIndex(AirQualityIndex index, AirQualityIndexMeasureResponseDto dto) {
        Integer qualityIndex = Integer.valueOf(dto.qualityIndex());
        if (qualityIndex < ALERT_THRESHOLD) {
            index.setAlertMessage(null);
            index.setAlert(false);
            return 0;
        }
        String alertMessage = AirQualityAlertUtils.determineAlertMessageWithArea(qualityIndex, dto.areaName());
        if (alertMessage != null) {
            index.setAlertMessage(alertMessage);
            index.setAlert(true);
            log.warn("⚠️ [ATMO] Alerte qualité air : {}", alertMessage);
            return 1;
        }
        index.setAlertMessage(null);
        index.setAlert(false);
        return 0;
    }

    /**
     * Envoie des alertes qualité de l'air aux utilisateurs ayant mis en favoris
     * les villes concernées par un indice ATMO dégradé (≥ seuil).
     */
    private void checkAndProcessAirQualityAlerts() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<AirQualityIndex> alertIndexes = indexRepository.findByAlertTrueAndMeasuredAtBetween(startOfDay, endOfDay);

        int notificationCount = 0;
        for (AirQualityIndex index : alertIndexes) {
            List<City> cities = cityRepository.findByAreaCode(index.getAreaCode());
            for (City city : cities) {
                ExternalAlertDto dto = new ExternalAlertDto(city.getId(), "AIR_QUALITY", index.getAlertMessage());
                externalAlertProcessingService.processExternalAlert(dto);
                notificationCount++;
            }
        }

        if (notificationCount > 0) {
            log.info("📨 [ATMO] {} notification(s) qualité air envoyée(s) pour {} zone(s)",
                    notificationCount, alertIndexes.size());
        }
    }

    /**
     * Parse le JSON retourné par l'API ATMO et extrait les données dans des DTOs.
     *
     * @param json      Le JSON à parser
     * @param dataField Le champ contenant les données (properties ou attributes)
     * @param dtoClass  La classe du DTO cible
     * @param <T>       Le type du DTO
     * @return La liste des DTOs parsés
     */
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

    /**
     * Groupe les mesures par station de mesure.
     *
     * @param measures La liste des mesures à grouper
     * @return Une map avec le code station comme clé et la liste des mesures comme valeur
     */
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

    /**
     * Récupère une station existante ou en crée une nouvelle.
     * La station doit être associée à une ville existante en base de données.
     *
     * @param dto Le DTO contenant les informations de la station
     * @return La station trouvée ou créée, null si aucune ville associée n'existe
     */
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

    /**
     * Récupère les données JSON depuis l'API ATMO.
     *
     * @param uri L'URI de l'endpoint à appeler
     * @return Le JSON récupéré, ou null en cas d'erreur
     */
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

    /**
     * Remplit les valeurs de polluants d'une mesure en fonction du nom du polluant.
     *
     * @param measurement   La mesure à remplir
     * @param pollutantName Le nom du polluant (PM10, PM2.5, NO2, O3, SO2)
     * @param value         La valeur de concentration du polluant
     */
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
     * Indique si le service de synchronisation est activé.
     *
     * @return true si le service est activé et qu'il y a moins de 3 erreurs consécutives
     */
    @Override
    public boolean isEnabled() {
        return enabled && consecutiveErrors < 3;
    }

    /**
     * Retourne l'intervalle de synchronisation configuré.
     *
     * @return La durée entre deux synchronisations
     */
    @Override
    public Duration getSyncInterval() {
        return Duration.ofHours(syncIntervalHours);
    }

    /**
     * Retourne la date et l'heure de la dernière synchronisation réussie.
     *
     * @return La date et l'heure de la dernière synchronisation
     */
    @Override
    public LocalDateTime getLastSync() {
        return lastSync;
    }

    /**
     * Retourne le nombre d'erreurs consécutives lors des synchronisations.
     *
     * @return Le nombre d'erreurs consécutives
     */
    @Override
    public int getConsecutiveErrors() {
        return consecutiveErrors;
    }
}