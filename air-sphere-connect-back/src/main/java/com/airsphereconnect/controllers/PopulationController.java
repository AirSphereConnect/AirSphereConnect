package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.response.PopulationResponseDto;
import com.airsphereconnect.mapper.PopulationMapper;
import com.airsphereconnect.services.PopulationService;
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

@Tag(name = "Historique Population", description = "API de consultation de l'historique démographique des villes")
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping("/api/history")
public class PopulationController {

    private final PopulationService populationService;
    private final PopulationMapper populationMapper;

    public PopulationController(PopulationService populationService, PopulationMapper populationMapper) {
        this.populationService = populationService;
        this.populationMapper = populationMapper;
    }

    @Operation(
            summary = "Récupérer l'historique de population d'une ville",
            description = "Retourne l'évolution démographique historique d'une ville donnée"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique de population récupéré",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PopulationResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ville non trouvée", content = @Content)
    })
    @GetMapping("/{cityName}")
    public List<PopulationResponseDto> getHistoryByCityName(
            @Parameter(description = "Nom de la ville", example = "Toulouse", required = true)
            @PathVariable String cityName) {
        return populationService.getHistoryByCityName(cityName).stream().map(populationMapper::toDto).toList();
    }
}
