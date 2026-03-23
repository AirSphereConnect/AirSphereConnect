package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.ApiRegionResponseDto;
import com.airsphereconnect.entities.Region;

public class ApiRegionMapper {
    private ApiRegionMapper() {}

    public static Region toEntity(ApiRegionResponseDto apiRegionResponseDto) {
        if( apiRegionResponseDto == null ) return null;

        Region region = new Region();
        region.setCode(apiRegionResponseDto.code());
        region.setName(apiRegionResponseDto.nom());

        return region;
    }
}
