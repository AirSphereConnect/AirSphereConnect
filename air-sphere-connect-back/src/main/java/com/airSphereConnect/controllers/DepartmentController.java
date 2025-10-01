package com.airSphereConnect.controllers;


import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.mapper.DepartmentMapper;
import com.airSphereConnect.services.DepartmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;


    public DepartmentController(DepartmentService departmentService, DepartmentMapper departmentMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    @GetMapping()
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentService.getAllDepartments()
                .stream()
                .map(departmentMapper::toDto)
                .toList();
    }

    @GetMapping("/departmentName/{name}")
    public DepartmentResponseDto getDepartmentByName(@PathVariable String name) {
        return departmentMapper.toDto(departmentService.getDepartmentByName(name));
    }

    @GetMapping("/departmentCode/{code}")
    public DepartmentResponseDto getDepartmentByCode(@PathVariable String code) {
        return departmentMapper.toDto(departmentService.getDepartmentByCode(code));
    }
}
