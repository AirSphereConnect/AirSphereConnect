package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Description d'un phénomène météorologique associé à une alerte ou aux conditions météo actuelles.")
@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDescriptionDto(
        @Schema(description = "Catégorie principale du phénomène météorologique.", example = "Clouds")
        @JsonProperty("main") String main,

        @Schema(
                description = "Description détaillée du phénomène météo.",
                example = "nuageux avec quelques éclaircies"
        )
        @JsonProperty("description") String description,

        @Schema(
                description = "Code de l’icône associée au phénomène météo.",
                example = "03d"
        )
        @JsonProperty("icon") String icon
) {

}
