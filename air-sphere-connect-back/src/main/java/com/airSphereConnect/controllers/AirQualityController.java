package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.AirQualityIndexResponseDto;
import com.airSphereConnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airSphereConnect.dtos.response.AirQualityStationResponseDto;
import com.airSphereConnect.dtos.response.AirQualityDataResponseDto;
import com.airSphereConnect.services.implementations.AirQualityServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Qualité de l'air", description = "API de surveillance de la qualité de l'air - Indices ATMO, polluants (PM2.5, PM10, NO2, O3, SO2) et historiques")
@RestController
//@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/api/air-quality")
public class AirQualityController {

    private final AirQualityServiceImpl airQualityService;

    public AirQualityController(AirQualityServiceImpl airQualityService) {
        this.airQualityService = airQualityService;
    }

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
     * Récupère la dernière mesure de polluants pour une ville
     *
     * @param cityName Nom de la ville
     * @return Dernière mesure (PM2.5, PM10, NO2, O3, SO2)
     */
    @GetMapping("/city/{cityName}/latest-measurement")
    public ResponseEntity<AirQualityMeasurementResponseDto> getLatestMeasurement(
            @PathVariable String cityName) {

        AirQualityMeasurementResponseDto measurement =
                airQualityService.getLatestMeasurementForCity(cityName);
        return ResponseEntity.ok(measurement);
    }

    /**
     * Récupère le dernier indice ATMO pour une ville
     *
     * @param cityName Nom de la ville
     * @return Indice ATMO (1-6) avec message
     */
    @GetMapping("/city/{cityName}/latest-index")
    public ResponseEntity<AirQualityIndexResponseDto> getLatestIndexQuality(
            @PathVariable String cityName) {

        return ResponseEntity.ok(
                airQualityService.getLatestIndexQualityForCity(cityName)
        );
    }

    /**
     * Récupère les données complètes (mesures + indice) pour une ville
     *
     * @param cityName Nom de la ville
     * @return Données complètes de qualité de l'air
     */
    @GetMapping("/city/{cityName}/complete")
    public ResponseEntity<AirQualityDataResponseDto> getCompleteDataForCity(
            @PathVariable String cityName) {

        AirQualityDataResponseDto data = airQualityService.getCompleteDataForCity(cityName);
        return ResponseEntity.ok(data);
    }

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
     * Récupère les N plus grandes villes du département avec leurs données air quality
     *
     * @param departmentCode Code département
     * @param limit Nombre de villes à retourner (par défaut 2)
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
