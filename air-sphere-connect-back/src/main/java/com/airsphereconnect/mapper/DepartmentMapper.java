package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.DepartmentResponseDto;
import com.airsphereconnect.entities.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    // From Entity to Dto
    public DepartmentResponseDto toDto(Department department) {
        if (department == null) return null;

        return new DepartmentResponseDto(
                department.getId(),
                department.getName(),
                department.getCode());
    }
}