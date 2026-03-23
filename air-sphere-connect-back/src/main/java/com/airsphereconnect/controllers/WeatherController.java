package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.response.WeatherResponseDto;
import com.airsphereconnect.entities.WeatherMeasurement;
import com.airsphereconnect.mapper.WeatherMapper;
import com.airsphereconnect.services.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Météo", description = "API de gestion des données météorologiques - Températures, conditions actuelles et historiques")
@RestController
//@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherMapper weatherMapper;

    public WeatherController(WeatherService weatherService, WeatherMapper weatherMapper) {
        this.weatherService = weatherService;
        this.weatherMapper = weatherMapper;
    }

    @Operation(
            summary = "Récupérer les dernières mesures météo (paginées)",
            description = "Retourne les mesures météo les plus récentes avec pagination, triées par date décroissante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des mesures météo récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WeatherResponseDto.class)))
    })
    @GetMapping("/latest")
    public List<WeatherResponseDto> getAllWeather(
            @Parameter(description = "Numéro de la page (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("measuredAt").descending());
        Page<WeatherMeasurement> pageResult = weatherService.findAll(pageable); // Tu peux créer cette méthode si besoin
        return pageResult.stream()
                .map(weatherMapper::toDto)
                .toList();
    }

    @Operation(
            summary = "Récupérer la météo actuelle d'une ville",
            description = "Retourne la dernière mesure météo disponible pour une ville spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Données météo trouvées",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WeatherResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content)
    })
    @GetMapping("/city/{id}")
    public WeatherResponseDto getWeatherCityById(
            @Parameter(description = "Identifiant de la ville", example = "1", required = true)
            @PathVariable Long id) {
        WeatherMeasurement weather = weatherService.getWeatherByCityId(id);

        return weatherMapper.toDto(weather);
    }

    @Operation(
            summary = "Récupérer l'historique météo d'une ville",
            description = "Retourne toutes les mesures météo historiques pour une ville donnée"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique météo récupéré",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = WeatherResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content)
    })
    @GetMapping("/city/history/{id}")
    public List<WeatherResponseDto> getWeatherHistoryByCityId(
            @Parameter(description = "Identifiant de la ville", example = "1", required = true)
            @PathVariable Long id) {
        List<WeatherMeasurement> measurements = weatherService.getWeatherHistoryByCityId(id);
        return measurements.stream()
                .map(weatherMapper::toDto)
                .toList();
    }


}


