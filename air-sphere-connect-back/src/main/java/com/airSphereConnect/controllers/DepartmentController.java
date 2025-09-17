package com.airSphereConnect.controllers;


import com.airSphereConnect.dtos.request.DepartmentRequestDto;
import com.airSphereConnect.mapper.DepartmentMapper;
import com.airSphereConnect.services.DepartmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DepartmentController {

    private final DepartmentService departmentService;


    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments")
    public List<DepartmentRequestDto> getAllDepartments() {
        return departmentService.getAllDepartments()
                .stream()
                .map(DepartmentMapper::toDto)
                .toList();
    }

    @GetMapping("/departments/{name}")
    public DepartmentRequestDto getDepartmentByName(@PathVariable String name) {
        return DepartmentMapper.toDto(departmentService.getDepartmentByName(name));
    }

    @GetMapping("/departments/{code}")
    public DepartmentRequestDto getDepartmentByCode(@PathVariable String code) {
        return DepartmentMapper.toDto(departmentService.getDepartmentByCode(code));
    }
}
