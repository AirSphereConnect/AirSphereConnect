package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * DTO Données complètes d'une ville envoyées à l'utilisateur
 *
 * @param id identifiant de la ville
 * @param inseeCode Code insee de la ville
 * @param name Nom de la ville
 * @param postalCode Code postal de la ville
 * @param latitude Latitude de la ville
 * @param longitude Longitude de la ville
 * @param areaCode Code Zone de la ville
 * @param departmentName Nom du département de la ville
 * @param population Population de la ville
 */
@Schema(description = "Données envoyées d'une ville")
public record CityResponseDto (
        @Schema(description = "Identifiant de la ville", example = "1")
        Long id,

        @Schema(description = "Code insee de la ville", example = "34512")
        String inseeCode,

        @Schema(description = "Nom de la ville", example = "Montpellier")
        String name,

        @Schema(description = "Code postal de la ville", example = "34090")
        String postalCode,

        @Schema(description = "Latitude de la ville", example = "43.62505")
        Double latitude,

        @Schema(description = "Longitude de la ville", example = "3.862038")
        Double longitude,

        @Schema(description = "Code Zone de la ville", example = "243400017")
        String areaCode,

        @Schema(description = "Nom du département de la ville", example = "Hérault")
        String departmentName,

        @Schema(description = "Population de la ville", example = "350")
        Integer population
) {}


