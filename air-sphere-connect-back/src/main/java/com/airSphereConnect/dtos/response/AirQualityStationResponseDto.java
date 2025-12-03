package com.airSphereConnect.dtos.response;

import com.airSphereConnect.entities.AirQualityStation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informations d'une station de mesure de la qualité de l'air")
public record AirQualityStationResponseDto(
        @Schema(description = "Identifiant unique de la station", example = "1")
        Long id,

        @Schema(description = "Nom de la station de mesure", example = "Montpellier - Chaptal Urbain")
        String name,

        @Schema(description = "Code unique de la station", example = "FR50201")
        String code,

        @Schema(description = "Code de la zone géographique", example = "243400017")
        String areaCode,

        @Schema(description = "Nom de la ville de la station", example = "Montpellier")
        String city
) {
}