package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.Department;
import com.airSphereConnect.repositories.DepartmentRepository;
import com.airSphereConnect.services.DepartmentService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
    public Department getDepartmentByCode(String code) {
        return null;
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not " +
                "found with id: " + id));
    }

    @Override
    public Department getDepartmentByName(String name) {
        return departmentRepository.getDepartmentByName(name).orElseThrow(() -> new ResourceNotFoundException(
                "Department not found with name : " + name));
    }
}
