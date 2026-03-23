package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.response.ForumResponseDto;
import com.airsphereconnect.dtos.response.ForumRubricResponseDto;
import com.airsphereconnect.services.ForumRubricService;
import com.airsphereconnect.services.ForumService;
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

@Tag(name = "Forum", description = "API de gestion du forum communautaire - Forums et rubriques de discussion")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
@RequestMapping("/api/forums")
public class ForumController {
    private final ForumService forumService;
    private final ForumRubricService forumRubricService;

    public ForumController(ForumService forumService, ForumRubricService forumRubricService) {
        this.forumService = forumService;
        this.forumRubricService = forumRubricService;
    }

    @Operation(
            summary = "Récupérer un forum par ID",
            description = "Retourne les informations d'un forum spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forum trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Forum non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ForumResponseDto> getForumById(
            @Parameter(description = "Identifiant du forum", example = "1", required = true)
            @PathVariable Long id) {
        ForumResponseDto response = forumService.getForumById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Récupérer les rubriques d'un forum",
            description = "Retourne toutes les rubriques de discussion associées à un forum"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rubriques récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumRubricResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Forum non trouvé", content = @Content)
    })
    @GetMapping("/{id}/rubrics")
    public ResponseEntity<List<ForumRubricResponseDto>> getRubricsByForumId(
            @Parameter(description = "Identifiant du forum", example = "1", required = true)
            @PathVariable Long id) {
        List<ForumRubricResponseDto> rubrics = forumRubricService.getRubricsByForumId(id);
        return ResponseEntity.ok(rubrics);
    }




}


