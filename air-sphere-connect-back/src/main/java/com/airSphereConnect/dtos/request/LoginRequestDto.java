package com.airSphereConnect.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données reçue lors d'une connexion utilisateur")
public record LoginRequestDto(
        @Schema(description = "nom d'utilisateur de connexion", example = "Cyril")
        String username,
        @Schema(description = "Mot de passe de connexion de l'utilisateur", example = "Azerty123/")
        String password) {
}
