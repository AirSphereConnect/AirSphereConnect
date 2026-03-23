package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.FavoritesAlertsDto;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.FavoritesAlertsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Configuration Alertes Favoris", description = "API de gestion des configurations d'alertes pour les villes favorites")
@RestController
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequestMapping("/api/alert/configurations")
public class FavoritesAlertsController {

    private final FavoritesAlertsService favoritesAlertsService;
    private final UserRepository userRepository;


    public FavoritesAlertsController(FavoritesAlertsService favoritesAlertsService, UserRepository userRepository) {
        this.favoritesAlertsService = favoritesAlertsService;
        this.userRepository = userRepository;
    }


    @Operation(
            summary = "Créer une configuration d'alerte",
            description = "Permet à un utilisateur de configurer des alertes pour ses villes favorites"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration créée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoritesAlertsDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PostMapping
    public FavoritesAlertsDto create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Configuration de l'alerte",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FavoritesAlertsDto.class))
            )
            @RequestBody FavoritesAlertsDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsernameAndDeletedAtIsNull(userDetails.getUsername())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé"));

        return favoritesAlertsService.createAlertConfig(user.getId(),dto);
    }

    @Operation(
            summary = "Récupérer toutes les configurations d'alertes (Admin uniquement)",
            description = "Retourne toutes les configurations d'alertes de tous les utilisateurs"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des configurations récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoritesAlertsDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<FavoritesAlertsDto> getAllFavoritesAlerts() {
        return favoritesAlertsService.getAllFavoritesAlerts();
    }

    @Operation(
            summary = "Récupérer les configurations d'alertes d'un utilisateur",
            description = "Retourne toutes les configurations d'alertes d'un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des configurations récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoritesAlertsDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
    })
    @GetMapping("/user/{userId}")
    public List<FavoritesAlertsDto> getUserAlerts(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @PathVariable Long userId) {
        return favoritesAlertsService.getUserAlerts(userId);
    }

    @Operation(
            summary = "Mettre à jour une configuration d'alerte",
            description = "Permet de modifier une configuration d'alerte existante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FavoritesAlertsDto.class))),
            @ApiResponse(responseCode = "404", description = "Configuration ou utilisateur non trouvé", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PutMapping("/{id}")
    public FavoritesAlertsDto update(
            @Parameter(description = "Identifiant de la configuration", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelle configuration de l'alerte",
                    required = true,
                    content = @Content(schema = @Schema(implementation = FavoritesAlertsDto.class))
            )
            @RequestBody FavoritesAlertsDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsernameAndDeletedAtIsNull(userDetails.getUsername())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé"));

        return favoritesAlertsService.updateAlertConfig(dto, user.getId(), id);
    }


    @Operation(
            summary = "Supprimer une configuration d'alerte",
            description = "Permet de supprimer une configuration d'alerte"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Configuration supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Configuration non trouvée", content = @Content)
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "Identifiant de la configuration à supprimer", example = "1", required = true)
            @PathVariable Long id) {
        favoritesAlertsService.deleteAlertConfig(id);
    }
}
