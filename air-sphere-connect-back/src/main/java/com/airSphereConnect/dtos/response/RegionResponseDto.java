package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données envoyées de la région")
public record RegionResponseDto(
        @Schema(description = "Identifiant de la région", example = "1")
        Long id,
        @Schema(description = "Nom de la région", example = "Occitanie")
        String name,
        @Schema(description = "Code du Département", example = "11")
        String code) {
}
