package com.airSphereConnect.controllers;


import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.dtos.response.RegionResponseDto;
import com.airSphereConnect.mapper.DepartmentMapper;
import com.airSphereConnect.mapper.RegionMapper;
import com.airSphereConnect.services.DepartmentService;
import com.airSphereConnect.services.RegionService;
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

    @GetMapping()
    public List<RegionResponseDto> getAllDepartments() {
        return regionService.getAllRegions()
                .stream()
                .map(regionMapper::toDto)
                .toList();
    }

    @GetMapping("/regionName/{name}")
    public RegionResponseDto getDepartmentByName(@PathVariable String name) {
        return regionMapper.toDto(regionService.getRegionByName(name));
    }

    @GetMapping("/regionCode/{code}")
    public RegionResponseDto getDepartmentByCode(@PathVariable String code) {
        return regionMapper.toDto(regionService.getRegionByCode(code));
    }
}
