package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.ApiDepartmentResponseDto;
import com.airsphereconnect.entities.Department;
import com.airsphereconnect.entities.Region;

public class ApiDepartmentMapper {
    private ApiDepartmentMapper() {}

    public static Department toEntity(ApiDepartmentResponseDto dto, Region region) {
        if (dto == null) return null;

        Department department = new Department();
        department.setName(dto.name());
        department.setCode(dto.code());
        department.setRegion(region);
        return department;

    }
}
