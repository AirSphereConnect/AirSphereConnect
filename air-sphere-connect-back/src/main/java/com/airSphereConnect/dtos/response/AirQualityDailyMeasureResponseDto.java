package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

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