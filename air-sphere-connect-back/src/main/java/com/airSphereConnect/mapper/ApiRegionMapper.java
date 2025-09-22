package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.ApiRegionResponseDto;
import com.airSphereConnect.entities.Region;

public class ApiRegionMapper {

    public static Region toEntity(ApiRegionResponseDto apiRegionResponseDto) {
        if( apiRegionResponseDto == null ) return null;

        Region region = new Region();
        region.setCode(apiRegionResponseDto.code());
        region.setName(apiRegionResponseDto.nom());

        return region;
    }
}
