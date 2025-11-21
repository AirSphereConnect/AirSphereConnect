package com.airSphereConnect.controllers;


import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.mapper.DepartmentMapper;
import com.airSphereConnect.services.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;


    public DepartmentController(DepartmentService departmentService, DepartmentMapper departmentMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    /**
     * Récupère tous les départements.
     * @return responseEntity contenant la liste des départements au format DepartmentResponseDto
     */
    @GetMapping()
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartments() {
        List<DepartmentResponseDto> departments = departmentService.getAllDepartments()
                .stream()
                .map(departmentMapper::toDto)
                .toList();

        return ResponseEntity.ok(departments);
    }

    /**
     * Récupère un département par son nom.
     * @param name le nom du département
     * @return responseEntity contenant le département au format DepartmentResponseDto
     */
    @GetMapping("/departmentName/{name}")
    public ResponseEntity<DepartmentResponseDto> getDepartmentByName(@PathVariable String name) {
        Department department = departmentService.getDepartmentByName(name);
        return ResponseEntity.ok(departmentMapper.toDto(department));
    }

    /**
     * Récupère un département par son code.
     * @param code le code du département
     * @return responseEntity contenant le département au format DepartmentResponseDto
     */
    @GetMapping("/departmentCode/{code}")
    public ResponseEntity<DepartmentResponseDto> getDepartmentByCode(@PathVariable String code) {
        Department department = departmentService.getDepartmentByCode(code);
        return ResponseEntity.ok(departmentMapper.toDto(department));
    }
}
