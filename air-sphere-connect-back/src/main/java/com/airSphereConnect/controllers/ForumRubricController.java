package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.services.ForumRubricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rubriques du Forum", description = "API de gestion des rubriques/catégories du forum")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
@RequestMapping("/api/forum-rubrics")
public class ForumRubricController {
    private final ForumRubricService forumRubricService;

    public ForumRubricController(ForumRubricService forumRubricService) {
        this.forumRubricService = forumRubricService;
    }

    @Operation(
            summary = "Récupérer toutes les rubriques actives",
            description = "Retourne la liste de toutes les rubriques actives du forum"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rubriques récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumRubricResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<ForumRubricResponseDto>> getAllActiveRubrics() {
        List<ForumRubricResponseDto> responses = forumRubricService.getAllActiveRubrics();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Récupérer une rubrique par ID",
            description = "Retourne les détails d'une rubrique spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rubrique trouvée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumRubricResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Rubrique non trouvée", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ForumRubricResponseDto> getRubricById(
            @Parameter(description = "Identifiant de la rubrique", example = "1", required = true)
            @PathVariable Long id) {
        ForumRubricResponseDto response = forumRubricService.getRubricById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Récupérer les rubriques d'un utilisateur",
            description = "Retourne toutes les rubriques créées par un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rubriques récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumRubricResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ForumRubricResponseDto>> getRubricsByUser(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @PathVariable Long userId) {
        List<ForumRubricResponseDto> responses = forumRubricService.getRubricsByCurrentUser(userId);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Créer une nouvelle rubrique",
            description = "Permet à un utilisateur de créer une nouvelle rubrique de forum"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rubrique créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumRubricResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/new/{userId}")
    public ResponseEntity<ForumRubricResponseDto> createRubric(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de la nouvelle rubrique",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForumRubricRequestDto.class))
            )
            @Valid @RequestBody ForumRubricRequestDto request,
            @Parameter(description = "Identifiant de l'utilisateur créateur", example = "1", required = true)
            @PathVariable Long userId) {

        ForumRubricResponseDto response = forumRubricService.createRubric(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Modifier une rubrique",
            description = "Permet à l'auteur d'une rubrique de la modifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rubrique modifiée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumRubricResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rubrique non trouvée", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ForumRubricResponseDto> updateRubric(
            @Parameter(description = "Identifiant de la rubrique à modifier", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de la rubrique",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForumRubricRequestDto.class))
            )
            @Valid @RequestBody ForumRubricRequestDto request,
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @RequestParam Long userId) {

        ForumRubricResponseDto response = forumRubricService.updateRubric(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer une rubrique",
            description = "Permet à l'auteur ou à un admin de supprimer une rubrique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rubrique supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rubrique non trouvée", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRubric(
            @Parameter(description = "Identifiant de la rubrique à supprimer", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @RequestParam Long userId) {

        forumRubricService.deleteRubric(id, userId);
        return ResponseEntity.noContent().build();
    }
}
