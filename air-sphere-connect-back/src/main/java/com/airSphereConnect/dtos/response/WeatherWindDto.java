package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherWindDto(
        @JsonProperty("speed") Double speed,
        @JsonProperty("deg") Double deg
) {

}
