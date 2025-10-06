package com.airSphereConnect.dtos.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        String areaName
) {
}
