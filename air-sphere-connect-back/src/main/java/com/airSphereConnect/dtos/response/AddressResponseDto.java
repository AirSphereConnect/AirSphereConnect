package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Informations d'une adresse utilisateur")
public class AddressResponseDto {
    @Schema(description = "Identifiant unique de l'adresse", example = "1")
    private Long id;

    @Schema(description = "Nom de la rue", example = "12 Rue de la République")
    private String street;

    @Schema(description = "Date de création de l'adresse")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière mise à jour")
    private LocalDateTime updatedAt;

    @Schema(description = "Ville associée à l'adresse", example = "Montpellier")
    private CityIdResponseDto city;

    public AddressResponseDto() {
    }

    public AddressResponseDto(Long id, String street, LocalDateTime createdAt, CityIdResponseDto city) {
        this.id = id;
        this.street = street;
        this.createdAt = createdAt;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CityIdResponseDto getCity() {
        return city;
    }

    public void setCity(CityIdResponseDto city) {
        this.city = city;
    }
}
