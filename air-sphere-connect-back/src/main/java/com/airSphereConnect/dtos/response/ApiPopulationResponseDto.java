package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Informations de population d'une ville")
public record ApiPopulationResponseDto(
        @Schema(description = "Code INSEE de la ville", example = "34172")
        @JsonProperty("code") String code,

        @Schema(description = "Nom de la ville", example = "Montpellier")
        @JsonProperty("nom") String name,

        @Schema(description = "Population de la ville", example = "299096")
        @JsonProperty("population") Integer population
) {
}

