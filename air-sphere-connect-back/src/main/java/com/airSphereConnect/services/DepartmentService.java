package com.airSphereConnect.services;

import com.airSphereConnect.entities.Department;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    List<Department> getAllDepartments();

    Department getDepartmentByCode(String code);

    Department getDepartmentByName(String name);
}
