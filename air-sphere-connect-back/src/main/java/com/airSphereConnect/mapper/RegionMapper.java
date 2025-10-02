package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.dtos.response.RegionResponseDto;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import org.springframework.stereotype.Component;

@Component
public class RegionMapper {

    // From Entity to Dto
    public RegionResponseDto toDto(Region region) {
        if (region == null) return null;

        return new RegionResponseDto(
                region.getId(),
                region.getCode(),
                region.getName());
    }
}
