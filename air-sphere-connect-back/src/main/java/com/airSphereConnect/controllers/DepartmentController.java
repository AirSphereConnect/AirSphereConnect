package com.airSphereConnect.controllers;


import com.airSphereConnect.entities.Department;
import com.airSphereConnect.services.DepartmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentDto departmentDto;


    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    GetMapping("/departments")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
        // TODO convert to DTO
    }
}
