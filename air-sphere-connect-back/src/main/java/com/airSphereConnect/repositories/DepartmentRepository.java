package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> getDepartmentByNameIgnoreCase(String name);
    Optional<Department> getDepartmentByCodeIgnoreCase(String code);
}
