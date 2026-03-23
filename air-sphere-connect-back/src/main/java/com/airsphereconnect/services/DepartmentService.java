package com.airsphereconnect.services;

import com.airsphereconnect.entities.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartmentByCode(String code);

    Department getDepartmentByName(String name);
}
