package com.airsphereconnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Informations provenant d'une alerte météo issue d'un fournisseur externe")
@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherAlertDto(
        @Schema(description = "Nom du service ayant émis l’alerte météo", example = "OpenWeather")
        @JsonProperty("sender_name") String senderName,

        @Schema(description = "Type d'évènement météo signalé.", example = "Alerte Orage")
        @JsonProperty("event") String event,

        @Schema(description = "Description détaillée de l’événement météo : intensité, durée, zones concernées, etc", example = "Orages intenses attendus dans la soirée.")
        @JsonProperty("description") String description
) {
}
