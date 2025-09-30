package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiRegionResponseDto;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.mapper.ApiRegionMapper;
import com.airSphereConnect.repositories.RegionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Transactional
public class RegionSyncService {

    private final WebClient webClient;
    private final RegionRepository regionRepository;

    public RegionSyncService(WebClient populationApiWebClient, RegionRepository regionRepository) {
        this.webClient = populationApiWebClient;
        this.regionRepository = regionRepository;
    }

    public void importRegions() {
        List<ApiRegionResponseDto> regions = webClient.get()
                .uri("/regions")
                .retrieve()
                .bodyToFlux(ApiRegionResponseDto.class)
                .collectList()
                .block();

        if(regions != null && !regions.isEmpty()) {
            List<Region> regionsList = regions.stream().map(ApiRegionMapper::toEntity).toList();

            regionRepository.saveAll(regionsList);
        }
    }
}
