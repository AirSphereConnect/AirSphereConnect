package com.airsphereconnect.controllers;


import com.airsphereconnect.dtos.response.DepartmentResponseDto;
import com.airsphereconnect.entities.Department;
import com.airsphereconnect.mapper.DepartmentMapper;
import com.airsphereconnect.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
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

    /**
     * Récupère tous les départements.
     *
     * @return responseEntity contenant la liste des départements au format DepartmentResponseDto
     */
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
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartments() {
        List<DepartmentResponseDto> departments = departmentService.getAllDepartments()
                .stream()
                .map(departmentMapper::toDto)
                .toList();

        return ResponseEntity.ok(departments);
    }

    /**
     * Récupère un département par son nom.
     *
     * @param name le nom du département
     * @return responseEntity contenant le département au format DepartmentResponseDto
     */
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
    public ResponseEntity<DepartmentResponseDto> getDepartmentByName(
            @Parameter(description = "Nom du département", example = "Haute-Garonne", required = true)
            @PathVariable String name) {
        Department department = departmentService.getDepartmentByName(name);
        return ResponseEntity.ok(departmentMapper.toDto(department));
    }

    /**
     * Récupère un département par son code.
     *
     * @param code le code du département
     * @return responseEntity contenant le département au format DepartmentResponseDto
     */
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
    public ResponseEntity<DepartmentResponseDto> getDepartmentByCode(
            @Parameter(description = "Code du département", example = "31", required = true)
            @PathVariable String code) {
        Department department = departmentService.getDepartmentByCode(code);
        return ResponseEntity.ok(departmentMapper.toDto(department));
    }
}
