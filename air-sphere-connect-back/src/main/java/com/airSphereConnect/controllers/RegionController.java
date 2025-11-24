package com.airSphereConnect.controllers;


import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.dtos.response.RegionResponseDto;
import com.airSphereConnect.mapper.DepartmentMapper;
import com.airSphereConnect.mapper.RegionMapper;
import com.airSphereConnect.services.DepartmentService;
import com.airSphereConnect.services.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Régions", description = "API de gestion des régions françaises - Consultation et recherche par nom ou code")
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

    @Operation(
            summary = "Récupérer toutes les régions (Admin uniquement)",
            description = "Retourne la liste complète des régions françaises"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des régions récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegionResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping()
    public List<RegionResponseDto> getAllDepartments() {
        return regionService.getAllRegions()
                .stream()
                .map(regionMapper::toDto)
                .toList();
    }

    @Operation(
            summary = "Rechercher une région par nom",
            description = "Retourne les informations d'une région en utilisant son nom"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Région trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Région non trouvée", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping("/regionName/{name}")
    public RegionResponseDto getDepartmentByName(
            @Parameter(description = "Nom de la région", example = "Occitanie", required = true)
            @PathVariable String name) {
        return regionMapper.toDto(regionService.getRegionByName(name));
    }

    @Operation(
            summary = "Rechercher une région par code",
            description = "Retourne les informations d'une région en utilisant son code"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Région trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Région non trouvée", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping("/regionCode/{code}")
    public RegionResponseDto getDepartmentByCode(
            @Parameter(description = "Code de la région", example = "76", required = true)
            @PathVariable String code) {
        return regionMapper.toDto(regionService.getRegionByCode(code));
    }
}
