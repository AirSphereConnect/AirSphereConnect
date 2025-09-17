package com.airSphereConnect.dtos.mapper;

import com.airSphereConnect.dtos.DepartmentDto;
import com.airSphereConnect.entities.Department;

public class DepartmentMapper {

    public static DepartmentDto toDto(Department department) {
        if (department == null) return null;

        return new DepartmentDto(department.getCode(), department.getName());
    }

    public static Department toEntity(DepartmentDto departmentDto) {
        if (departmentDto == null) return null;

        Department department = new Department();
        department.setId(departmentDto.get);
    }

}