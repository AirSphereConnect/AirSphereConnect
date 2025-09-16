package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.Department;
import com.airSphereConnect.repositories.DepartmentRepository;
import com.airSphereConnect.services.DepartmentService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow( () => new ResourceNotFoundException("Department not " +
                "found with id: " + id));
    }

    @Override
    public Department createDepartment(Department department) {
        return null;
    }

    @Override
    public Department updateDepartment(Department department) {
        return null;
    }

    @Override
    public void deleteDepartment(Long id) {

    }
}
