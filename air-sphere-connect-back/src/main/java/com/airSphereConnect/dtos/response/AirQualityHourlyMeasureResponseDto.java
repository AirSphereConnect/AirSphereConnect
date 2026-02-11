
package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * DTO de réponse pour les mesures horaires de polluants de l'API ATMO Occitanie.
 * Représente une mesure horaire d'un polluant spécifique provenant du service
 * "Mesure_horaire_(30j)_Region_Occitanie_Polluants_Reglementaires_1" de l'API ATMO.
 * Utilisé lors de la synchronisation des données depuis l'API externe vers la base locale.
 * Fournit une méthode utilitaire pour convertir le timestamp Unix en LocalDateTime.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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
     * Convertit le timestamp Unix (millisecondes) en LocalDateTime.
     * Utilise le fuseau horaire système pour la conversion.
     *
     * @return La date et heure de la mesure convertie, ou null si le timestamp est null
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
