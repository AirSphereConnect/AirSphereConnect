package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informations d'un département français")
public record ApiDepartmentResponseDto (
    @Schema(description = "Code du département", example = "34")
    @JsonProperty("code") String code,

    @Schema(description = "Nom du département", example = "Hérault")
    @JsonProperty("nom") String name,

    @Schema(description = "Code de la région", example = "76")
    @JsonProperty("codeRegion") String regionCode
){}