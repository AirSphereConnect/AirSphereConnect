package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ApiCityResponseDto(
        @JsonProperty("code") String inseeCode,
        @JsonProperty("nom") String name,
        @JsonProperty("codesPostaux") List<String> postalCodes,
        @JsonProperty("codeEpci") String zoneCode,
        @JsonProperty("centre") CentreDto centre,
        @JsonProperty("codeDepartement") String departmentCode,
        @JsonProperty("population") Integer population
) {
}

