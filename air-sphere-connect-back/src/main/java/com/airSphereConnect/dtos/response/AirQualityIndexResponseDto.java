package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Indice de qualité de l'air avec informations détaillées")
public record AirQualityIndexResponseDto(
        @Schema(description = "Identifiant unique de l'indice", example = "1")
        Long id,

        @Schema(description = "Indice de qualité de l'air (1-6)", example = "2")
        Integer qualityIndex,

        @Schema(description = "Label textuel de la qualité", example = "Moyen")
        String qualityLabel,

        @Schema(description = "Couleur associée à l'indice", example = "#51CCAA")
        String qualityColor,

        @Schema(description = "Date et heure de la mesure")
        LocalDateTime measuredAt,

        @Schema(description = "Code de la zone géographique", example = "34172")
        String areaCode,

        @Schema(description = "Nom de la zone géographique", example = "Montpellier")
        String areaName,

        @Schema(description = "Source des données", example = "ATMO Occitanie")
        String source,

        @Schema(description = "Message d'alerte éventuel", example = "Aucune alerte")
        String alertMessage
) {

}
