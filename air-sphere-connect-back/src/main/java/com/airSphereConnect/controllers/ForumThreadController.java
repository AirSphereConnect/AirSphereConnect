package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.ForumThreadRequestDto;
import com.airSphereConnect.dtos.response.ForumThreadResponseDto;
import com.airSphereConnect.services.ForumThreadService;
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

@Tag(name = "Threads du Forum", description = "API de gestion des fils de discussion (threads) - Création, lecture, modification et suppression de conversations")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
@RequestMapping("/api/forum-threads")
public class ForumThreadController {
    private final ForumThreadService forumThreadService;

    public ForumThreadController(ForumThreadService forumThreadService) {
        this.forumThreadService = forumThreadService;
    }

    @Operation(
            summary = "Récupérer tous les threads actifs",
            description = "Retourne la liste de tous les fils de discussion actifs (non supprimés)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des threads récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumThreadResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<ForumThreadResponseDto>> getAllActiveThreads() {
        List<ForumThreadResponseDto> responses = forumThreadService.getAllActiveThreads();
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Récupérer un thread par ID",
            description = "Retourne les détails d'un fil de discussion spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thread trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumThreadResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Thread non trouvé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ForumThreadResponseDto> getThreadById(
            @Parameter(description = "Identifiant du thread", example = "1", required = true)
            @PathVariable Long id) {
        ForumThreadResponseDto response = forumThreadService.getThreadById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Récupérer les threads d'un utilisateur",
            description = "Retourne tous les fils de discussion créés par un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des threads récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumThreadResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ForumThreadResponseDto>> getThreadsByUser(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @PathVariable Long userId) {
        List<ForumThreadResponseDto> responses = forumThreadService.getThreadsByCurrentUser(userId);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Créer un nouveau thread",
            description = "Permet à un utilisateur connecté de créer un nouveau fil de discussion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thread créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumThreadResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/new/{userId}")
    public ResponseEntity<ForumThreadResponseDto> createThread(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du nouveau thread",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForumThreadRequestDto.class))
            )
            @Valid @RequestBody ForumThreadRequestDto request,
            @Parameter(description = "Identifiant de l'utilisateur créateur", example = "1", required = true)
            @PathVariable Long userId) {

        ForumThreadResponseDto response = forumThreadService.createThread(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Modifier un thread",
            description = "Permet à l'auteur d'un thread de le modifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thread modifié avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumThreadResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - L'utilisateur n'est pas l'auteur", content = @Content),
            @ApiResponse(responseCode = "404", description = "Thread non trouvé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ForumThreadResponseDto> updateThread(
            @Parameter(description = "Identifiant du thread à modifier", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations du thread",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForumThreadRequestDto.class))
            )
            @RequestBody ForumThreadRequestDto request,
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @RequestParam Long userId) {

        ForumThreadResponseDto response = forumThreadService.updateThread(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un thread",
            description = "Permet à l'auteur ou à un admin de supprimer un thread"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Thread supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Thread non trouvé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThread(
            @Parameter(description = "Identifiant du thread à supprimer", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @RequestParam Long userId) {

        forumThreadService.deleteThread(id, userId);
        return ResponseEntity.noContent().build();
    }
}
