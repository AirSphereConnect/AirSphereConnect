package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour les mesures journalières de polluants de l'API ATMO Occitanie.
 * Représente une mesure journalière d'un polluant spécifique provenant du service
 * "Mesure_journaliere_(30j)_Region_Occitanie_Polluants_Reglementaires" de l'API ATMO.
 * Utilisé lors de la synchronisation des données depuis l'API externe vers la base locale.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AirQualityDailyMeasureResponseDto(
        @JsonProperty("insee_com")
        Integer inseeCode,

        @JsonProperty("nom_station")
        String nomStation,

        @JsonProperty("code_station")
        String codeStation,

        @JsonProperty("nom_poll")
        String polluantName,

        @JsonProperty("valeur")
        Double polluantValue,

        @JsonProperty("unite")
        String polluantUnit,

        @JsonProperty("date_debut")
        Long dateDebutTimestamp,

        LocalDateTime measuredAt
) {
}