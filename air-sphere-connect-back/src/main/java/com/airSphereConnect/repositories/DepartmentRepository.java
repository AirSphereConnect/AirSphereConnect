package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> getDepartmentByName(String name);
}
