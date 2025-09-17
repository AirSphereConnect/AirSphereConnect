package com.airSphereConnect.controllers;


import com.airSphereConnect.dtos.DepartmentDto;
import com.airSphereConnect.services.DepartmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentDto departmentDto;


    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments")
    public List<DepartmentDto> getAllDepartments() {
        return departmentService.getAllDepartments()
                .stream()
                .map(dep -> dep.toDto())
                .toList();
        // TODO convert to DTO
    }

    @GetMapping("/departments/{name}")
    public DepartmentDto getDepartmentByName(String name) {
        return departmentService.getDepartmentByName(name)
                .toDto();
    }

    @GetMapping("/departments/{code}")
    public DepartmentDto getDepartmentByCode(String code) {
        return departmentService.getDepartmentByCode(code).toDto();
    }
}
