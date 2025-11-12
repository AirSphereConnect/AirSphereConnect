
package com.airSphereConnect.dtos.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * DTO pour l'API horaire 30 jours ATMO Occitanie
 * Service : Mesure_horaire_(30j)_Region_Occitanie_Polluants_Reglementaires_1
 */
public record AirQualityHourlyMeasureResponseDto(
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
        Long dateDebutTimestamp
) {
    /**
     * Convertit le timestamp Unix (millisecondes) en LocalDateTime
     */
    public LocalDateTime getMeasuredAt() {
        if (dateDebutTimestamp == null) {
            return null;
        }

        return Instant.ofEpochMilli(dateDebutTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
