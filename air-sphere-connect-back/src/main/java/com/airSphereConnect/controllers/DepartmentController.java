package com.airSphereConnect.controllers;


import com.airSphereConnect.dtos.response.DepartmentResponseDto;
import com.airSphereConnect.mapper.DepartmentMapper;
import com.airSphereConnect.services.DepartmentService;
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

@Tag(name = "Départements", description = "API de gestion des départements français - Consultation et recherche par nom ou code")
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

    @Operation(
            summary = "Récupérer tous les départements (Admin uniquement)",
            description = "Retourne la liste complète des départements français"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des départements récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepartmentResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping()
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentService.getAllDepartments()
                .stream()
                .map(departmentMapper::toDto)
                .toList();
    }

    @Operation(
            summary = "Rechercher un département par nom",
            description = "Retourne les informations d'un département en utilisant son nom"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Département trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepartmentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Département non trouvé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping("/departmentName/{name}")
    public DepartmentResponseDto getDepartmentByName(
            @Parameter(description = "Nom du département", example = "Haute-Garonne", required = true)
            @PathVariable String name) {
        return departmentMapper.toDto(departmentService.getDepartmentByName(name));
    }

    @Operation(
            summary = "Rechercher un département par code",
            description = "Retourne les informations d'un département en utilisant son code (2 chiffres)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Département trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepartmentResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Département non trouvé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping("/departmentCode/{code}")
    public DepartmentResponseDto getDepartmentByCode(
            @Parameter(description = "Code du département", example = "31", required = true)
            @PathVariable String code) {
        return departmentMapper.toDto(departmentService.getDepartmentByCode(code));
    }
}
