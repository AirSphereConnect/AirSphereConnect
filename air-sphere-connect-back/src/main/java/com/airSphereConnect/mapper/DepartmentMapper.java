package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.entities.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    // From Entity to Dto
    public DepartmentResponseDto toDto(Department department) {
        if (department == null) return null;

        return new DepartmentResponseDto(
                department.getId(),
                department.getCode(),
                department.getName());
    }
}