package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.RegionResponseDto;
import com.airsphereconnect.entities.Region;
import org.springframework.stereotype.Component;

@Component
public class RegionMapper {

    // From Entity to Dto
    public RegionResponseDto toDto(Region region) {
        if (region == null) return null;

        return new RegionResponseDto(
                region.getId(),
                region.getName(),
                region.getCode());
    }
}
