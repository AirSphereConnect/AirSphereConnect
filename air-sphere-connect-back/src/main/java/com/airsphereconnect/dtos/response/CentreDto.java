package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public record CentreDto(
        @JsonProperty("type") String type,
        @JsonProperty("coordinates") Double[] coordinates
) {
    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof CentreDto(var t, var c)
                && Objects.equals(type, t) && Arrays.equals(coordinates, c);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(type) + Arrays.hashCode(coordinates);
    }

    @Override
    public String toString() {
        return "CentreDto[type=" + type + ", coordinates=" + Arrays.toString(coordinates) + "]";
    }

    public Double latitude() {
        return coordinates != null && coordinates.length == 2 ? coordinates[1] : null;
    }

    public Double longitude() {
        return coordinates != null && coordinates.length == 2 ? coordinates[0] : null;
    }
}
