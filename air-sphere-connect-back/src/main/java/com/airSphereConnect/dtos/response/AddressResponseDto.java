package com.airSphereConnect.dtos.response;

import com.airSphereConnect.entities.City;

import java.time.LocalDateTime;

public class AddressResponseDto {
    private Long id;
    private String street;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private City city;

    public AddressResponseDto() {
    }

    public AddressResponseDto(Long id, String street, LocalDateTime createdAt, City city) {
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

    public City getCity() {
        return city;
    }
    public void setCity(City city) {
        this.city = city;
    }
}
