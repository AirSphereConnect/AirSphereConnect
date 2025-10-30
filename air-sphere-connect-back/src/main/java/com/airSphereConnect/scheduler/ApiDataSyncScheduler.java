package com.airSphereConnect.scheduler;

import com.airSphereConnect.services.api.DataSyncService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableScheduling
public class ApiDataSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(ApiDataSyncScheduler.class);

    private final List<DataSyncService> syncServices;
    private final SyncMetrics syncMetrics;


    public ApiDataSyncScheduler(List<DataSyncService> syncServices, SyncMetrics syncMetrics) {
        this.syncServices = syncServices;
        this.syncMetrics = syncMetrics;
        log.info("🚀 Scheduler initialisé avec {} service(s)", syncServices.size());
    }


    @EventListener(ApplicationReadyEvent.class)
    public void initSync() {
        log.info("🔍 Démarrage de la synchronisation initiale...");

        // 1️⃣ Synchroniser les services de base dans l'ordre : REGION → DEPARTMENT → CITY
        syncBaseDataServices();

        // 2️⃣ Synchroniser les autres services (qui dépendent des villes)
        syncServices.stream()
                .filter(service -> {
                    String name = service.getServiceName();
                    // Exclure les services de base déjà synchronisés
                    return !name.equals("REGION") && !name.equals("DEPARTMENT") && !name.equals("CITY");
                })
                .filter(service -> service.getLastSync() == null)
                .filter(DataSyncService::isEnabled)
                .forEach(service -> {
                    log.info("🔄 Première sync: {}", service.getServiceName());
                    try {
                        syncService(service);
                    } catch (Exception e) {
                        log.error("❌ Erreur sync initiale {}: {}", service.getServiceName(), e.getMessage());
                    }
                });

        log.info("✅ Synchronisation initiale terminée");
    }

    /**
     * Synchronise les services de base dans l'ordre : REGION → DEPARTMENT → CITY
     * Ces services doivent être synchronisés avant les autres
     */
    private void syncBaseDataServices() {
        log.info("📋 Synchronisation des données de base...");

        // Ordre d'exécution : REGION → DEPARTMENT → CITY
        String[] baseServicesOrder = {"REGION", "DEPARTMENT", "CITY"};

        for (String serviceName : baseServicesOrder) {
            syncServices.stream()
                    .filter(service -> service.getServiceName().equals(serviceName))
                    .filter(service -> service.getLastSync() == null)
                    .filter(DataSyncService::isEnabled)
                    .findFirst()
                    .ifPresent(service -> {
                        log.info("🔄 Sync données de base: {}", service.getServiceName());
                        try {
                            syncService(service);
                        } catch (Exception e) {
                            log.error("❌ Erreur critique sync {}: {}", service.getServiceName(), e.getMessage(), e);
                            throw new RuntimeException("Échec critique de la synchronisation de " + serviceName, e);
                        }
                    });
        }

        log.info("✅ Données de base synchronisées");
    }

    /**
     * Cycle de synchronisation global toutes les 12 heures
     * Parcourt tous les services enregistrés et exécute leur synchronisation si activée
     */
    @Scheduled(fixedRate = 43200000, initialDelay = 180000) // 12h, délai initial 3min
    public void globalSynchronization() {
        log.info("🔄 [{}] Début cycle de synchronisation (12h)", LocalDateTime.now());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        syncServices.stream()
                .filter(DataSyncService::isEnabled)
                .forEach(service -> {
                    log.info("✅ Synchronisation du service: {}", service.getServiceName());
                    try {
                        syncService(service);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        log.error("❌ Erreur synchronisation {}: {}", service.getServiceName(), e.getMessage());
                    }
                });

        log.info("✅ Cycle 12h terminé - Succès: {}, Erreurs: {}", successCount.get(), errorCount.get());
        syncMetrics.recordGlobalSync(successCount, errorCount);
    }


    /**
     * 🌅 Synchronisation des bulletins du matin à 10h00
     * Seuls les services AirQuality et Weather sont synchronisés
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void morningBulletinSync() {
        log.info("🌅 [{}] Synchronisation bulletin du matin (10h)", LocalDateTime.now());

        List<String> morningServices = Arrays.asList("AIR_QUALITY", "WEATHER");

        syncServices.stream()
                .filter(service -> morningServices.contains(service.getServiceName()))
                .filter(DataSyncService::isEnabled)
                .forEach(service -> {
                    System.out.println("Sync matinale: " + service.getServiceName());
                    syncService(service);
                });

        log.info("✅ Synchronisation matinale terminée");
    }

    /*
     * 🎯 Synchronisation annuelle des données de recensement le 27 décembre
     */
    @Scheduled(cron = "0 0 2 27 12 *")
    public void annualRecensementSync() {
        log.info("📊 [{}] Synchronisation annuelle recensement", LocalDateTime.now());

        syncServices.stream()
                .filter(s -> s.getServiceName().equals("POPULATION"))
                .filter(DataSyncService::isEnabled)
                .findFirst()
                .ifPresent(service -> {
                    System.out.println("🔄 Sync annuelle: " + service.getServiceName());
                    syncService(service);
                });

        log.info("✅ Synchronisation annuelle terminée");
    }

    /**
     * Synchronise un service
     */
    private void syncService(DataSyncService service) {
        long start = System.currentTimeMillis();

        try {
            service.syncData();
            long duration = System.currentTimeMillis() - start;

            log.info("✅ {} synchronisé en {}ms", service.getServiceName(), duration);
            syncMetrics.recordServiceSync(service.getServiceName(), duration, true);


        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;

            log.error("❌ Erreur {}: {}", service.getServiceName(), e.getMessage());
            syncMetrics.recordServiceSync(service.getServiceName(), duration, false);

            throw new RuntimeException("Sync failed for " + service.getServiceName(), e);
        }
    }

    /**
     * API pour forcer la sync d'un service spécifique
     */
    public void forceSyncService(String serviceName) {
        log.info("🔧 Synchronisation forcée: {}", serviceName);

        syncServices.stream()
                .filter(s -> s.getServiceName().equals(serviceName))
                .findFirst()
                .ifPresentOrElse(
                        this::syncService,
                        () -> {
                            log.error("❌ Service non trouvé: {}", serviceName);
                            throw new IllegalArgumentException("Service inconnu: " + serviceName);
                        }
                );
    }

    /**
     * API pour forcer la sync de tous les services
     */
    public void forceSyncAll() {
        log.info("🔧 Synchronisation forcée de tous les services");

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();

        syncServices.stream()
                .filter(DataSyncService::isEnabled)
                .forEach(service -> {
                    try {
                        syncService(service);
                        successCount.getAndIncrement();
                    } catch (Exception e) {
                        errorCount.getAndIncrement();
                    }
                });

        log.info("✅ Sync forcée terminée - Succès: {}, Erreurs: {}",
                successCount.get(), errorCount.get());
    }
}

