package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CentreDto(
        @JsonProperty("type") String type,
        @JsonProperty("coordinates") Double[] coordinates
) {
    public Double latitude() {
        return coordinates != null && coordinates.length == 2 ? coordinates[1] : null;
    }

    public Double longitude() {
        return coordinates != null && coordinates.length == 2 ? coordinates[0] : null;
    }
}
