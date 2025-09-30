package com.airSphereConnect.controllers;

import com.airSphereConnect.scheduler.ApiDataSyncScheduler;
import com.airSphereConnect.scheduler.SyncMetrics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des synchronisations d'APIs externes
 * Permet de forcer les synchronisations et de consulter les métriques
 */
@RestController
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
    @PostMapping("/force/{serviceName}")
    public ResponseEntity<String> forceSync(@PathVariable String serviceName) {
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
    @GetMapping("/metrics/{serviceName}")
    public ResponseEntity<SyncMetrics.ServiceStats> getServiceMetrics(@PathVariable String serviceName) {
        return ResponseEntity.ok(syncMetrics.getServiceStats(serviceName));
    }

}