package com.airsphereconnect.controllers;

import com.airsphereconnect.services.api.WeatherSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Administration", description = "API d'administration - Fonctions réservées aux administrateurs (synchronisation manuelle, etc.)")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {

    private final WeatherSyncService weatherService;

    public AdminController(WeatherSyncService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(
            summary = "Rafraîchir manuellement les données météo",
            description = "Force la synchronisation immédiate des données météo pour toutes les villes depuis l'API externe (Admin uniquement)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Synchronisation lancée avec succès",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Réservé aux administrateurs", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la synchronisation", content = @Content)
    })
    @PostMapping("/refresh-weather")
    public ResponseEntity<String> refreshWeatherNow() {
        weatherService.fetchAndStoreWeatherForAllCities();
        return ResponseEntity.ok("Météo mise à jour manuellement");
    }
}

