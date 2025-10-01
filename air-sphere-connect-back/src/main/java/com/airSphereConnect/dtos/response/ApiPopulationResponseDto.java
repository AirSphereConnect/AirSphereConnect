package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ApiPopulationResponseDto(
        @JsonProperty("code") String code,
        @JsonProperty("nom") String name,
        @JsonProperty("population") Integer population
) {
}

