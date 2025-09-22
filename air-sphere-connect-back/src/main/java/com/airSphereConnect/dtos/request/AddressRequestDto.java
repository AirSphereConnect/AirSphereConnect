package com.airSphereConnect.dtos.request;

import com.airSphereConnect.entities.City;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressRequestDto {
    @NotBlank(message = "{address.street.notBlank}")
    @Size(min = 1, max = 50, message = "{address.street.size}")
    @Pattern(
            regexp = "^[0-9]{0,5}\\s?[A-Za-zÀ-ÖØ-öø-ÿ0-9'’\\-.,/() ]+$",
            message = "{address.street.pattern}"
    )
    @Column(name = "street", nullable = false, length = 100)
    private String street;

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

