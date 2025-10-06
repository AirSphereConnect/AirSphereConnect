package com.airSphereConnect.dtos.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

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

        LocalDateTime measuredAt
) {
}