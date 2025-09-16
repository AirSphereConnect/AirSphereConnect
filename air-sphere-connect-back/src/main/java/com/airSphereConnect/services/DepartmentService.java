package com.airSphereConnect.services;

import com.airSphereConnect.entities.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    List<Department> getAllDepartments();
    Optional<Department> getDepartmentById(Long id);

    Optional<Department> createDepartment(Department department);
    Optional <Department> updateDepartment(Department department);
    void deleteDepartment(Long id);


}
