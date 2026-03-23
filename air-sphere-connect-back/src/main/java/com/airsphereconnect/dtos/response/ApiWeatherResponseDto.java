package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema(description = "Données météorologiques complètes depuis l'API OpenWeather")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiWeatherResponseDto(
    @Schema(description = "Données principales (température, pression, humidité)")
    @JsonProperty("main") WeatherMainDto weatherMainDto,

    @Schema(description = "Données du vent (vitesse, direction)")
    @JsonProperty("wind") WeatherWindDto weatherWindDto,

    @Schema(description = "Descriptions météo (conditions, icônes)")
    @JsonProperty("weather") List<WeatherDescriptionDto> weatherDescriptionDto,

    @Schema(description = "Alertes météorologiques actives")
    @JsonProperty("alerts") List<WeatherAlertDto> weatherAlertDto
) {}
