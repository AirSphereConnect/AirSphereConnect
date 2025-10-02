package com.airSphereConnect.scheduler;

import com.airSphereConnect.services.api.CitySyncService;
import com.airSphereConnect.services.api.DepartmentSyncService;
import com.airSphereConnect.services.api.PopulationSyncService;
import com.airSphereConnect.services.api.RegionSyncService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ApiRecensementSyncScheduler {

    private final CitySyncService citySyncService;
    private final DepartmentSyncService departmentSyncService;
    private final RegionSyncService regionSyncService;
    private final PopulationSyncService populationSyncService;

    public ApiRecensementSyncScheduler(CitySyncService citySyncService, DepartmentSyncService departmentSyncService, RegionSyncService regionSyncService, PopulationSyncService populationSyncService) {
        this.citySyncService = citySyncService;
        this.departmentSyncService = departmentSyncService;
        this.regionSyncService = regionSyncService;
        this.populationSyncService = populationSyncService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        regionSyncService.importRegions();
        departmentSyncService.importDepartments();
        citySyncService.importCitiesOccitanie();
        populationSyncService.importPopulations();
    }

    @Scheduled(cron = "0 0 2 27 12 *")
    public void scheduledImport() {
        regionSyncService.importRegions();
        departmentSyncService.importDepartments();
        citySyncService.importCitiesOccitanie();
    }
}
