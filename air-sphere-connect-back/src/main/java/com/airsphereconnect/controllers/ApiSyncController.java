package com.airsphereconnect.controllers;

import com.airsphereconnect.scheduler.ApiDataSyncScheduler;
import com.airsphereconnect.scheduler.SyncMetrics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des synchronisations d'APIs externes
 * Permet de forcer les synchronisations et de consulter les métriques
 */
@Tag(name = "Synchronisation APIs (Admin)", description = "API de gestion de la synchronisation avec les services externes - Force les syncs et consulte les métriques")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/sync")
public class ApiSyncController {

    private final ApiDataSyncScheduler scheduler;
    private final SyncMetrics syncMetrics;

    public ApiSyncController(ApiDataSyncScheduler scheduler,
                             SyncMetrics syncMetrics) {
        this.scheduler = scheduler;
        this.syncMetrics = syncMetrics;
    }

    /**
     * 🎯 Forcer la synchronisation de tous les services actifs
     */
    @Operation(
            summary = "Forcer la synchronisation de tous les services",
            description = "Lance manuellement la synchronisation de tous les services externes (météo, qualité de l'air, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Synchronisation lancée avec succès",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la synchronisation", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PostMapping("/force-all")
    public ResponseEntity<String> forceSyncAll() {
        try {
           scheduler.forceSyncAll();
            return ResponseEntity.ok("✅ Tous les services synchronisés");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Erreur: " + e.getMessage());
        }
    }

    /**
     * 🎯 Forcer la synchronisation d'un service spécifique
     */
    @Operation(
            summary = "Forcer la synchronisation d'un service spécifique",
            description = "Lance manuellement la synchronisation d'un service externe spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Synchronisation réussie",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Nom de service invalide", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la synchronisation", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PostMapping("/force/{serviceName}")
    public ResponseEntity<String> forceSync(
            @Parameter(description = "Nom du service à synchroniser", example = "weather", required = true)
            @PathVariable String serviceName) {
        try {
            scheduler.forceSyncService(serviceName);
            return ResponseEntity.ok("✅ Sync réussie: " + serviceName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Erreur: " + e.getMessage());
        }
    }

    /**
     * 📊 Obtenir les métriques globales de synchronisation
     */
    @Operation(
            summary = "Consulter les métriques de synchronisation",
            description = "Retourne les métriques et statistiques globales de toutes les synchronisations effectuées"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Métriques récupérées",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> response = new HashMap<>();
        response.put("recentHistory", syncMetrics.getRecentHistory());
        response.put("serviceStats", syncMetrics.getAllStats());
        return ResponseEntity.ok(response);
    }

    /**
     * 📊 Obtenir les métriques d'un service spécifique
     */
    @Operation(
            summary = "Consulter les métriques d'un service spécifique",
            description = "Retourne les statistiques et métriques d'un service externe spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Métriques du service récupérées",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping("/metrics/{serviceName}")
    public ResponseEntity<SyncMetrics.ServiceStats> getServiceMetrics(
            @Parameter(description = "Nom du service", example = "weather", required = true)
            @PathVariable String serviceName) {
        return ResponseEntity.ok(syncMetrics.getServiceStats(serviceName));
    }

}
