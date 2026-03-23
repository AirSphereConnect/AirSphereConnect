package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.request.ForumPostRequestDto;
import com.airsphereconnect.dtos.response.ForumPostResponseDto;
import com.airsphereconnect.entities.enums.ReactionType;
import com.airsphereconnect.services.ForumPostService;
import com.airsphereconnect.services.PostReactionService;
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

@Tag(name = "Posts du Forum", description = "API de gestion des posts/messages du forum - Création, lecture, modification, suppression et réactions")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
@RequestMapping("/api/forum-posts")
public class ForumPostController {

    private final ForumPostService forumPostService;
    private final PostReactionService postReactionService;

    public ForumPostController(ForumPostService forumPostService, PostReactionService postReactionService) {
        this.forumPostService = forumPostService;
        this.postReactionService = postReactionService;
    }


    @Operation(
            summary = "Récupérer tous les posts actifs",
            description = "Retourne la liste de tous les posts actifs (non supprimés) du forum avec les compteurs de réactions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des posts récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumPostResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<List<ForumPostResponseDto>> getAllActivePosts(
            @Parameter(description = "ID de l'utilisateur actuel (pour afficher ses réactions)", example = "1")
            @RequestParam(required = false) Long currentUserId) {

        List<ForumPostResponseDto> posts = forumPostService.getAllActivePosts();
        enrichWithReactions(posts, currentUserId);
        return ResponseEntity.ok(posts);
    }

    private void enrichWithReactions(List<ForumPostResponseDto> posts, Long currentUserId) {
        for (ForumPostResponseDto post : posts) {
            enrichWithReactions(post, currentUserId);
        }
    }

    @Operation(
            summary = "Récupérer un post par ID",
            description = "Retourne les détails d'un post spécifique avec les compteurs de réactions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumPostResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Post non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ForumPostResponseDto> getPostWithReactions(
            @Parameter(description = "Identifiant du post", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID de l'utilisateur actuel", example = "1")
            @RequestParam(required = false) Long currentUserId) {

        ForumPostResponseDto post = forumPostService.getPostById(id);
        enrichWithReactions(post, currentUserId);
        return ResponseEntity.ok(post);
    }


    private void enrichWithReactions(ForumPostResponseDto post, Long currentUserId) {
        post.setLikeCount(postReactionService.countLikesByPost(post.getId()));
        post.setDislikeCount(postReactionService.countDislikesByPost(post.getId()));

        if (currentUserId != null) {
            ReactionType userReaction = postReactionService.getUserReaction(post.getId(), currentUserId);
            post.setCurrentUserReaction(userReaction);
        }
    }

    @Operation(
            summary = "Récupérer les posts d'un utilisateur",
            description = "Retourne tous les posts créés par un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des posts récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumPostResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ForumPostResponseDto>> getPostsByUser(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @PathVariable Long userId) {
        List<ForumPostResponseDto> responses = forumPostService.getPostsByUserId(userId);
        return ResponseEntity.ok(responses);
    }


    @Operation(
            summary = "Créer un nouveau post",
            description = "Permet à un utilisateur connecté de créer un nouveau post dans le forum"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumPostResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/new/{userId}")
    public ResponseEntity<ForumPostResponseDto> createPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Contenu du nouveau post",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForumPostRequestDto.class))
            )
            @Valid @RequestBody ForumPostRequestDto request,
            @Parameter(description = "Identifiant de l'utilisateur créateur", example = "1", required = true)
            @PathVariable Long userId) {

        ForumPostResponseDto response = forumPostService.createPost(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(
            summary = "Ajouter/retirer une réaction (like/dislike)",
            description = "Permet à un utilisateur de liker ou disliker un post (toggle : si déjà liké, retire le like)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réaction enregistrée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumPostResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Post ou utilisateur non trouvé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/{postId}/reaction")
    public ResponseEntity<ForumPostResponseDto> toggleReaction(
            @Parameter(description = "Identifiant du post", example = "1", required = true)
            @PathVariable Long postId,
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @RequestParam Long userId,
            @Parameter(description = "Type de réaction", example = "LIKE", required = true)
            @RequestParam ReactionType reaction) {

        postReactionService.toggleReaction(postId, userId, reaction);
        return getPostWithReactions(postId, userId);
    }

    @Operation(
            summary = "Modifier un post",
            description = "Permet à l'auteur d'un post de le modifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post modifié avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ForumPostResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé - L'utilisateur n'est pas l'auteur", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post non trouvé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<ForumPostResponseDto> updatePost(
            @Parameter(description = "Identifiant du post à modifier", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouveau contenu du post",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ForumPostRequestDto.class))
            )
            @Valid @RequestBody ForumPostRequestDto request,
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @RequestParam Long userId) {

        ForumPostResponseDto response = forumPostService.updatePost(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer un post",
            description = "Permet à l'auteur ou à un admin de supprimer un post"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Post non trouvé", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "Identifiant du post à supprimer", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @RequestParam Long userId) {

        forumPostService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }
}
