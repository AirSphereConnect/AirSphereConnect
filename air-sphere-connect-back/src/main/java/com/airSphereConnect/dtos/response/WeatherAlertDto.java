package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherAlertDto(
        @JsonProperty("sender_name") String senderName,
        @JsonProperty("event") String event,
        @JsonProperty("description") String description
) {
}
