package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Principales informations météo mesurées pour un lieu donné.")
@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherMainDto(
        @Schema(
                description = "Température actuelle en degrés Celsius.",
                example = "22.5"
        )
        @JsonProperty("temp") Double temp,

        @Schema(
                description = "Humidité de l'air en pourcentage.",
                example = "65"
        )
        @JsonProperty("humidity") Double humidity,

        @Schema(
                description = "Pression atmosphérique en hPa.",
                example = "1013"
        )
        @JsonProperty("pressure") Double pressure) {
}
