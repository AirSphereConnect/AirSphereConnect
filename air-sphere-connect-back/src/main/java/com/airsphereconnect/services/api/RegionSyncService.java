package com.airsphereconnect.services.api;

import com.airsphereconnect.dtos.response.ApiRegionResponseDto;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.entities.Region;
import com.airsphereconnect.mapper.ApiRegionMapper;
import com.airsphereconnect.repositories.RegionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegionSyncService implements DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(RegionSyncService.class);

    private final WebClient populationApiWebClient;
    private final RegionRepository regionRepository;

    @Value("${app.api.region.enabled:true}")
    private boolean enabled;

    @Value("${app.api.region.sync-interval-hours:720}")
    private int syncIntervalHours; // 30 jours par défaut

    private LocalDateTime lastSync;
    private int consecutiveErrors = 0;

    public RegionSyncService(WebClient populationApiWebClient, RegionRepository regionRepository) {
        this.populationApiWebClient = populationApiWebClient;
        this.regionRepository = regionRepository;
    }

    @Override
    public String getServiceName() {
        return "REGION";
    }

    @Override
    public void syncData() {
        log.info("🔄 Début synchronisation des régions...");
        try {
            importRegions();
            lastSync = LocalDateTime.now();
            consecutiveErrors = 0;
            log.info("✅ [REGION] Sync terminée");
        } catch (Exception e) {
            consecutiveErrors++;
            throw new GlobalException.SyncException("Sync failed for REGION", e);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled && consecutiveErrors < 3;
    }

    @Override
    public Duration getSyncInterval() {
        return Duration.ofHours(syncIntervalHours);
    }

    @Override
    public LocalDateTime getLastSync() {
        return lastSync;
    }

    @Override
    public int getConsecutiveErrors() {
        return consecutiveErrors;
    }

    // Retrieve all regions from API
    public void importRegions() {
        List<ApiRegionResponseDto> regions = populationApiWebClient.get()
                .uri("/regions")
                .retrieve()
                .bodyToFlux(ApiRegionResponseDto.class)
                .collectList()
                .block();

        if (regions == null || regions.isEmpty()) return;

        Map<String, Region> regionMap = regionRepository.findAll().stream()
                .collect(Collectors.toMap(Region::getCode,
                        r -> r));

        List<Region> regionList = regions.stream()
                .map(dto -> {
                    Region region = ApiRegionMapper.toEntity(dto);

                    Region regionToUpdate = regionMap.get(region.getCode());

                    if (regionToUpdate != null) {
                        regionToUpdate.setName(region.getName());
                        return regionToUpdate;
                    } else {
                        return region;
                    }
                })
                .toList();

        regionRepository.saveAll(regionList);
    }
}
