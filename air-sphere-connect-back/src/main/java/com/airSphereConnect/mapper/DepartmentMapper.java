package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.DepartmentRequestDto;
import com.airSphereConnect.entities.Department;

public class DepartmentMapper {


    // RequestDto -> Entity
    public static DepartmentRequestDto toDto(Department department) {
        if (department == null) return null;

        return new DepartmentRequestDto(
                // Afficher l'id pas pertinent
                department.getCode(),
                department.getName());
    }

    // Entity -> ResponseDto
    public static Department toEntity(DepartmentRequestDto departmentDto) {
        if (departmentDto == null) return null;

        Department department = new Department();
        department.setCode(departmentDto.getCode());
        department.setName(departmentDto.getName());

        return department;
    }

}