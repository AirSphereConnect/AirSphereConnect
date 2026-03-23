package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.response.AirQualityIndexResponseDto;
import com.airsphereconnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airsphereconnect.dtos.response.AirQualityStationResponseDto;
import com.airsphereconnect.dtos.response.AirQualityDataResponseDto;
import com.airsphereconnect.services.AirQualityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur REST pour la gestion de la qualité de l'air.
 * Expose les endpoints pour récupérer les stations, mesures de polluants, indices ATMO et historiques.
 * Les données proviennent de l'API ATMO Occitanie et sont stockées localement.
 */
@Tag(name = "Qualité de l'air", description = "API de surveillance de la qualité de l'air - Indices ATMO, polluants (PM2.5, PM10, NO2, O3, SO2) et historiques")
@RestController
//@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/api/air-quality")
public class AirQualityController {

    private final AirQualityService airQualityService;

    /**
     * Constructeur du contrôleur de qualité de l'air.
     *
     * @param airQualityService Le service de gestion de la qualité de l'air
     */
    public AirQualityController(AirQualityService airQualityService) {
        this.airQualityService = airQualityService;
    }

    /**
     * Récupère toutes les stations de mesure de la qualité de l'air.
     * Retourne la liste complète des stations avec leurs informations (nom, code, localisation).
     *
     * @return ResponseEntity contenant la liste de toutes les stations
     */
    @Operation(
            summary = "Récupérer toutes les stations de mesure",
            description = "Retourne la liste de toutes les stations de mesure de la qualité de l'air avec leurs coordonnées"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des stations récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AirQualityStationResponseDto.class)))
    })
    @GetMapping("/stations")
    public ResponseEntity<List<AirQualityStationResponseDto>> getAllStations() {
        List<AirQualityStationResponseDto> stations = airQualityService.getAllStations();

        return ResponseEntity.ok(stations);
    }

    /**
     * Récupère la dernière mesure de polluants pour une ville donnée.
     * Retourne les concentrations des différents polluants atmosphériques (PM2.5, PM10, NO2, O3, SO2).
     * Utilise une stratégie de fallback si aucune mesure directe n'est disponible pour la ville.
     *
     * @param cityName Le nom de la ville
     * @return ResponseEntity contenant la dernière mesure de polluants
     */
    @GetMapping("/city/{cityName}/latest-measurement")
    public ResponseEntity<AirQualityMeasurementResponseDto> getLatestMeasurement(
            @PathVariable String cityName) {

        AirQualityMeasurementResponseDto measurement =
                airQualityService.getLatestMeasurementForCity(cityName);
        return ResponseEntity.ok(measurement);
    }

    /**
     * Récupère le dernier indice ATMO de qualité de l'air pour une ville donnée.
     * L'indice ATMO varie de 1 (très bon) à 6 (extrêmement mauvais) et inclut un message d'alerte si nécessaire.
     * Utilise une stratégie de fallback si aucun indice direct n'est disponible pour la ville.
     *
     * @param cityName Le nom de la ville
     * @return ResponseEntity contenant l'indice ATMO (1-6) avec le message associé
     */
    @GetMapping("/city/{cityName}/latest-index")
    public ResponseEntity<AirQualityIndexResponseDto> getLatestIndexQuality(
            @PathVariable String cityName) {

        return ResponseEntity.ok(
                airQualityService.getLatestIndexQualityForCity(cityName)
        );
    }

    /**
     * Récupère les données complètes de qualité de l'air pour une ville donnée.
     * Retourne à la fois les mesures de polluants et l'indice ATMO dans un seul objet de réponse.
     * Utilise une stratégie de fallback si aucune donnée directe n'est disponible pour la ville.
     *
     * @param cityName Le nom de la ville
     * @return ResponseEntity contenant les données complètes de qualité de l'air (mesures + indice)
     */
    @GetMapping("/city/{cityName}/complete")
    public ResponseEntity<AirQualityDataResponseDto> getCompleteDataForCity(
            @PathVariable String cityName) {

        AirQualityDataResponseDto data = airQualityService.getCompleteDataForCity(cityName);
        return ResponseEntity.ok(data);
    }

    /**
     * Récupère l'historique des mesures de polluants pour une ville sur une période donnée.
     * Si les dates ne sont pas spécifiées, retourne les données des 30 derniers jours par défaut.
     * Les mesures incluent les concentrations de PM2.5, PM10, NO2, O3 et SO2.
     *
     * @param cityName  Le nom de la ville
     * @param startDate La date de début de la période (optionnelle, format ISO)
     * @param endDate   La date de fin de la période (optionnelle, format ISO)
     * @return ResponseEntity contenant la liste des mesures sur la période
     */
    @GetMapping("/city/{cityName}/history/measurements")
    public ResponseEntity<List<AirQualityMeasurementResponseDto>> getMeasurementsHistory(
            @PathVariable String cityName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
                airQualityService.getMeasurementsHistoryForCity(cityName, startDate, endDate)
        );
    }

    /**
     * Récupère l'historique des indices ATMO de qualité de l'air pour une ville sur une période donnée.
     * Si les dates ne sont pas spécifiées, retourne les données des 30 derniers jours par défaut.
     * Les indices ATMO varient de 1 (très bon) à 6 (extrêmement mauvais).
     *
     * @param cityName  Le nom de la ville
     * @param startDate La date de début de la période (optionnelle, format ISO)
     * @param endDate   La date de fin de la période (optionnelle, format ISO)
     * @return ResponseEntity contenant la liste des indices sur la période
     */
    @GetMapping("/city/{cityName}/history/indices")
    public ResponseEntity<List<AirQualityIndexResponseDto>> getIndicesHistory(
            @PathVariable String cityName,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
                airQualityService.getIndicesHistoryForCity(cityName, startDate, endDate)
        );
    }

    /**
     * Récupère les N plus grandes villes d'un département avec leurs données de qualité de l'air.
     * Les villes sont triées par population et limitées au nombre spécifié.
     * Retourne les données complètes (mesures + indices) pour chaque ville.
     *
     * @param departmentCode Le code du département (2 chiffres, ex: "31" pour Haute-Garonne)
     * @param limit          Le nombre de villes à retourner (par défaut 2)
     * @return ResponseEntity contenant la liste des données complètes pour les villes sélectionnées
     */
    @GetMapping("/department/{departmentCode}/top-cities")
    public ResponseEntity<List<AirQualityDataResponseDto>> getTopCitiesInDepartment(
            @PathVariable String departmentCode,
            @RequestParam(defaultValue = "2") int limit) {

        return ResponseEntity.ok(
                airQualityService.getTopCitiesWithDataInDepartment(departmentCode, limit)
        );
    }

}
