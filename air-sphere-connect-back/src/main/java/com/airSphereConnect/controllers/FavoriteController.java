package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.FavoriteService;
import com.airSphereConnect.services.implementations.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Favoris", description = "API de gestion des villes favorites - Ajout, suppression et consultation des villes préférées de l'utilisateur")
@RestController
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    public FavoriteController(FavoriteService favoriteService, UserRepository userRepository) {
        this.favoriteService = favoriteService;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Récupérer tous les favoris",
            description = "Retourne la liste complète des villes favorites de tous les utilisateurs (Admin uniquement)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des favoris récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoriteDto.class)))
    })
    @GetMapping
    public List<FavoriteDto> getAllFavorites() {
        return favoriteService.getAllFavorites();
    }

    @Operation(
            summary = "Récupérer un favori par ID",
            description = "Retourne les détails d'une ville favorite spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favori trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoriteDto.class))),
            @ApiResponse(responseCode = "404", description = "Favori non trouvé", content = @Content)
    })
    @GetMapping("/{id}")
    public FavoriteDto getFavoriteById(
            @Parameter(description = "Identifiant du favori", example = "1", required = true)
            @PathVariable Long id) {
        return favoriteService.getFavoriteById(id);
    }

    @Operation(
            summary = "Ajouter une ville aux favoris",
            description = "Permet à l'utilisateur connecté d'ajouter une ville à sa liste de favoris"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Favori créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoriteDto.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    @PostMapping("/new")
    public ResponseEntity<FavoriteDto> createFavorite(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations de la ville à ajouter aux favoris",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FavoriteDto.class))
            )
            @RequestBody FavoriteDto favoriteDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsernameAndDeletedAtIsNull(userDetails.getUsername())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé"));

        FavoriteDto created = favoriteService.createFavorite(user.getId(), favoriteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Mettre à jour un favori",
            description = "Permet de modifier les informations d'une ville favorite"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favori mis à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoriteDto.class))),
            @ApiResponse(responseCode = "404", description = "Favori non trouvé", content = @Content)
    })
    @PutMapping("/{id}")
    public FavoriteDto updateFavorite(
            @Parameter(description = "Identifiant du favori à modifier", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations du favori",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FavoriteDto.class))
            )
            @RequestBody FavoriteDto favoriteDto) {
        return favoriteService.updateFavorite(id, favoriteDto);
    }

    @Operation(
            summary = "Supprimer un favori",
            description = "Permet de retirer une ville de la liste des favoris"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favori supprimé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoriteDto.class))),
            @ApiResponse(responseCode = "404", description = "Favori non trouvé", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<FavoriteDto> deleteFavorite(
            @Parameter(description = "Identifiant du favori à supprimer", example = "1", required = true)
            @PathVariable Long id) {
        FavoriteDto deletedFavorite = favoriteService.deleteFavorite(id);
        return ResponseEntity.ok(deletedFavorite);
    }
}

