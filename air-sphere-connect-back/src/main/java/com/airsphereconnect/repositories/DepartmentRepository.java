package com.airsphereconnect.repositories;

import com.airsphereconnect.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByNameIgnoreCase(String name);
    Optional<Department> findByCodeIgnoreCase(String code);
}
