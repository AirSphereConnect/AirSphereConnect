package com.airSphereConnect.services;

import com.airSphereConnect.entities.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartmentByCode(String code);

    Department getDepartmentByName(String name);
}
