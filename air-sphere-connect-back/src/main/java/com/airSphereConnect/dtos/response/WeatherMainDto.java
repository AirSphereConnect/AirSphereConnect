package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherMainDto(
        @JsonProperty("temp") Double temp,
        @JsonProperty("humidity") Double humidity,
        @JsonProperty("pressure") Double pressure) {
}
