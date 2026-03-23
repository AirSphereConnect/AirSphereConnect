package com.airsphereconnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informations d'une région française")
public record ApiRegionResponseDto (
    @Schema(description = "Code de la région", example = "76")
    String code,

    @Schema(description = "Nom de la région", example = "Occitanie")
    String nom
){}
