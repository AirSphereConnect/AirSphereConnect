package com.airsphereconnect.services.implementations;

import com.airsphereconnect.entities.Department;
import com.airsphereconnect.repositories.DepartmentRepository;
import com.airsphereconnect.services.DepartmentService;
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
        return departmentRepository.findByCodeIgnoreCase(code).orElseThrow(() -> new ResourceNotFoundException(
                "Department with code " + code + " not found"));
    }

    @Override
    public Department getDepartmentByName(String name) {
        return departmentRepository.findByNameIgnoreCase(name).orElseThrow(() -> new ResourceNotFoundException(
                "Department not found with name : " + name));
    }
}