package com.airSphereConnect.scheduler;

import com.airSphereConnect.services.api.CitySyncService;
import com.airSphereConnect.services.api.DepartmentSyncService;
import com.airSphereConnect.services.api.RegionSyncService;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ApiRecensementSyncScheduler {

    private final CitySyncService citySyncService;
    private final DepartmentSyncService departmentSyncService;
    private final RegionSyncService regionSyncService;

    public ApiRecensementSyncScheduler(CitySyncService citySyncService, DepartmentSyncService departmentSyncService, RegionSyncService regionSyncService) {
        this.citySyncService = citySyncService;
        this.departmentSyncService = departmentSyncService;
        this.regionSyncService = regionSyncService;
    }

    @PostConstruct
    public void init() {
//        regionSyncService.importRegions();
//        departmentSyncService.importDepartments();
//        citySyncService.importCitiesOccitanie();
    }

    @Scheduled(cron = "0 0 2 27 12 *")
    public void scheduledImport() {
        regionSyncService.importRegions();
        departmentSyncService.importDepartments();
        citySyncService.importCitiesOccitanie();
    }
}
