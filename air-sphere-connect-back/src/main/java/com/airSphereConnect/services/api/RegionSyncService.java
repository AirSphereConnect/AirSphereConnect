package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiRegionResponseDto;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.mapper.ApiRegionMapper;
import com.airSphereConnect.repositories.RegionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegionSyncService {

    private final WebClient populationApiWebClient;
    private final RegionRepository regionRepository;

    public RegionSyncService(WebClient populationApiWebClient, RegionRepository regionRepository) {
        this.populationApiWebClient = populationApiWebClient;
        this.regionRepository = regionRepository;
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
