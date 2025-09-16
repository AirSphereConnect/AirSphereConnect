package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
