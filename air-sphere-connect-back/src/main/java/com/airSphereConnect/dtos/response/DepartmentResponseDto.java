package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données envoyées d'un département")
public record DepartmentResponseDto(
        @Schema(description = "Identifiant du Département", example = "1")
        Long id,
        @Schema(description = "Nom du Département", example = "Hérault")
        String name,
        @Schema(description = "Code du Département", example = "34")
        String code) {
}
