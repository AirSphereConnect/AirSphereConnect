package com.airSphereConnect.controllers;


import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.dtos.response.RegionResponseDto;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.mapper.DepartmentMapper;
import com.airSphereConnect.mapper.RegionMapper;
import com.airSphereConnect.services.DepartmentService;
import com.airSphereConnect.services.RegionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;
    private final RegionMapper regionMapper;


    public RegionController(RegionService regionService, RegionMapper regionMapper) {
        this.regionService = regionService;
        this.regionMapper = regionMapper;
    }

    /**
     * Récupère toutes les régions.
     * @return responseEntity contenant la liste des régions au format RegionResponseDto
     */
    @GetMapping()
    public ResponseEntity<List<RegionResponseDto>> getAllDepartments() {
        List<RegionResponseDto> region = regionService.getAllRegions()
                .stream()
                .map(regionMapper::toDto)
                .toList();

        return ResponseEntity.ok(region);
    }

    /**
     * Récupère une région par son nom.
     * @param name le nom de la région
     * @return responseEntity contenant la région au format RegionResponseDto
     */
    @GetMapping("/regionName/{name}")
    public ResponseEntity<RegionResponseDto> getDepartmentByName(@PathVariable String name) {
        Region region = regionService.getRegionByName(name);
        return ResponseEntity.ok(regionMapper.toDto(region));
    }

    /**
     * Récupère une région par son code.
     * @param code le code de la région
     * @return responseEntity contenant la région au format RegionResponseDto
     */
    @GetMapping("/regionCode/{code}")
    public ResponseEntity<RegionResponseDto> getDepartmentByCode(@PathVariable String code) {
        Region region = regionService.getRegionByCode(code);
        return ResponseEntity.ok(regionMapper.toDto(region));
    }
}
