package com.airsphereconnect.dtos.request;

import com.airsphereconnect.entities.City;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Adresse complète des utilisateurs reçue lors d'une saisie")
public class AddressRequestDto {
    @Schema(description = "Nom de la rue d'une ville", example = "1 rue de la fontaine")
    @NotBlank(message = "{address.street.notBlank}")
    @Size(min = 1, max = 50, message = "{address.street.size}")
    @Pattern(
            regexp = "^\\d{0,5}\\s?[A-Za-zÀ-ÖØ-öø-ÿ0-9’.,/() -]+$",
            message = "{address.street.pattern}"
    )
    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @Schema(description = "Nom de la ville", example = "Montpellier")
    private City city;

    public AddressRequestDto() {
    }

    public AddressRequestDto(String street,  City city) {
        this.street = street;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public City getCity() {
       return city;
    }
    public void setCity(City city) {
        this.city = city;
    }
}

