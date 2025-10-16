package com.airSphereConnect.dtos.response;

import java.time.LocalDateTime;

public class AddressResponseDto {
    private Long id;
    private String street;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
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
