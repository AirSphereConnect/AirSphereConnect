package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.ApiDepartmentResponseDto;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;

public class ApiDepartmentMapper {
    public static Department toEntity(ApiDepartmentResponseDto dto, Region region) {
        if (dto == null) return null;

        Department department = new Department();
        department.setName(dto.name());
        department.setCode(dto.code());
        department.setRegion(region);
        return department;

    }
}
