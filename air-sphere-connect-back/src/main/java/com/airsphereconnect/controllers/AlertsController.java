package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.AlertsDto;
import com.airsphereconnect.services.AlertsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Alertes (Admin)", description = "API d'administration des alertes environnementales - Envoi et gestion des notifications utilisateurs")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/alerts")
public class AlertsController {

    private final AlertsService alertsService;

    public AlertsController(AlertsService alertsService) {

        this.alertsService = alertsService;
    }

    @Operation(
            summary = "Envoyer des alertes aux utilisateurs",
            description = "Permet à un administrateur d'envoyer des alertes environnementales (pollution, météo) aux utilisateurs concernés"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alertes envoyées avec succès", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données d'alerte invalides", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Réservé aux administrateurs", content = @Content)
    })
    @PostMapping("/register")
    public void sendAlerts(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données de l'alerte à envoyer",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AlertsDto.class))
            )
            @RequestBody AlertsDto dto) {
        alertsService.sendAlerts(dto);
    }

    @Operation(
            summary = "Récupérer les alertes d'un utilisateur",
            description = "Retourne toutes les alertes reçues par un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des alertes récupérée",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlertsDto.class))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @GetMapping("/user/{userId}")
    public List<AlertsDto> getUserAlerts(
            @Parameter(description = "Identifiant de l'utilisateur", example = "1", required = true)
            @PathVariable Long userId) {
        return alertsService.getUserAlerts(userId);
    }
}

