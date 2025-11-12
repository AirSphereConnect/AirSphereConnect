package com.airSphereConnect.controllers;

import com.airSphereConnect.services.api.HistoricalDataLoaderService;
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
