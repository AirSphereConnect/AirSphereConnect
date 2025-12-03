package com.airSphereConnect.controllers;

import com.airSphereConnect.services.api.HistoricalDataLoaderService;
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

import java.util.Map;

/**
 * ⚡ Controller pour charger l'historique des données ATMO (usage ponctuel)
 * La sécurité est gérée par SecurityConfig (permitAll)
 */
@Tag(name = "Chargement Historique (Admin)", description = "API de chargement ponctuel des données historiques - À utiliser lors de l'initialisation uniquement")
@RestController
@RequestMapping("/api/admin/historical-data")
public class HistoricalDataLoaderController {

    private final HistoricalDataLoaderService loaderService;

    public HistoricalDataLoaderController(HistoricalDataLoaderService loaderService) {
        this.loaderService = loaderService;
    }

    /**
     * 🚀 Lancer le chargement des 30 derniers jours
     *
     * ATTENTION : À utiliser UNE SEULE FOIS lors de l'initialisation
     * Peut prendre plusieurs minutes selon la quantité de données
     */
    @Operation(
            summary = "Charger les 30 derniers jours de données historiques",
            description = "⚠️ ATTENTION : À utiliser UNE SEULE FOIS lors de l'initialisation du système. Charge les données historiques ATMO des 30 derniers jours. Peut prendre plusieurs minutes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chargement terminé avec succès",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Erreur lors du chargement", content = @Content)
    })
    @PostMapping("/load-30-days")
    public ResponseEntity<Map<String, String>> loadLast30Days() {
        try {
            loaderService.loadLast30DaysHistory();

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Chargement historique des 30 derniers jours terminé"
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Erreur lors du chargement : " + e.getMessage()
            ));
        }
    }
}
