package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO de réponse pour les indices de qualité de l'air de l'API ATMO Occitanie.
 * Représente un indice ATMO (1-6) provenant du service d'indices de qualité de l'air de l'API ATMO.
 * Contient l'indice numérique, le label textuel (Bon, Moyen, etc.), la couleur associée,
 * ainsi que les informations de zone géographique et la source des données.
 * Utilisé lors de la synchronisation des indices depuis l'API externe vers la base locale.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AirQualityIndexMeasureResponseDto(
        @JsonProperty("code_qual")
        String qualityIndex,

        @JsonProperty("lib_qual")
        String qualityLabel,

        @JsonProperty("coul_qual")
        String qualityColor,

        @JsonProperty("source")
        String source,

        @JsonProperty("code_zone")
        String areaCode,

        @JsonProperty("lib_zone")
        String areaName,

        @JsonProperty("date_ech")
        Long dateEchTimestamp
) {
}
