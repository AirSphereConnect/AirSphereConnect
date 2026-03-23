package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Informations sur le vent pour un lieu donné")
@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherWindDto(

        @Schema(description = "Vitesse du vent en mètres par seconde.", example = "5.5")
        @JsonProperty("speed") Double speed,

        @Schema(description = "Direction du vent en degrés.", example = "180" )
        @JsonProperty("deg") Double deg
) {

}
