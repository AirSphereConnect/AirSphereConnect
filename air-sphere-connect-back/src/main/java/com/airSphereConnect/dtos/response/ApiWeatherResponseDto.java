package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiWeatherResponseDto(
    @JsonProperty("main") WeatherMainDto weatherMainDto,
    @JsonProperty("wind") WeatherWindDto weatherWindDto,
    @JsonProperty("weather") WeatherDescriptionDto[] weatherDescriptionDto,
    @JsonProperty("alert") WeatherAlertDto[] weatherAlertDto

) {

}
