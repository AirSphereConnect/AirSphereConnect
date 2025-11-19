package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.AirQualityIndexResponseDto;
import com.airSphereConnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airSphereConnect.dtos.response.AirQualityStationResponseDto;
import com.airSphereConnect.dtos.response.AirQualityDataResponseDto;
import com.airSphereConnect.services.implementations.AirQualityServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
//@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequestMapping("/api/air-quality")
public class AirQualityController {

    private final AirQualityServiceImpl airQualityService;

    public AirQualityController(AirQualityServiceImpl airQualityService) {
        this.airQualityService = airQualityService;
    }

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
