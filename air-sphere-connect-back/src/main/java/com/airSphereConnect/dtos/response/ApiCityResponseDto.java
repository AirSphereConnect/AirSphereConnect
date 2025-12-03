package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Informations complètes d'une ville française depuis l'API")
public record ApiCityResponseDto(
        @Schema(description = "Code INSEE de la ville", example = "34172")
        @JsonProperty("code") String inseeCode,

        @Schema(description = "Nom de la ville", example = "Montpellier")
        @JsonProperty("nom") String name,

        @Schema(description = "Liste des codes postaux", example = "[\"34000\", \"34070\", \"34080\", \"34090\"]")
        @JsonProperty("codesPostaux") List<String> postalCodes,

        @Schema(description = "Code EPCI (intercommunalité)", example = "243400017")
        @JsonProperty("codeEpci") String zoneCode,

        @Schema(description = "Coordonnées du centre de la ville")
        @JsonProperty("centre") CentreDto centre,

        @Schema(description = "Code du département", example = "34")
        @JsonProperty("codeDepartement") String departmentCode,

        @Schema(description = "Population de la ville", example = "299096")
        @JsonProperty("population") Integer population
) {
}

