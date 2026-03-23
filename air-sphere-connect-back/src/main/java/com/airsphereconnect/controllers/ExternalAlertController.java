package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.ExternalAlertDto;
import com.airsphereconnect.services.ExternalAlertProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Alertes Externes (Admin)", description = "API de réception des alertes provenant de systèmes externes (webhooks)")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/external-alerts")
public class ExternalAlertController {
    private static final Logger logger = LoggerFactory.getLogger(ExternalAlertController.class);
    private final ExternalAlertProcessingService processingService;

    public ExternalAlertController(ExternalAlertProcessingService processingService) {
        this.processingService = processingService;
    }

    @Operation(
            summary = "Recevoir une alerte externe (webhook)",
            description = "Endpoint webhook pour recevoir des alertes environnementales provenant de systèmes externes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerte reçue et traitée avec succès", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données d'alerte invalides", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    @PostMapping("/receive")
    public void receiveAlert(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données de l'alerte externe",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ExternalAlertDto.class))
            )
            @RequestBody ExternalAlertDto alert) {
        logger.debug("Réception alerte externe : cityId={}, type={}, message={}", alert.getCityId(), alert.getType(), alert.getMessage());
        processingService.processExternalAlert(alert);
    }
}
