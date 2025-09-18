package com.airSphereConnect.dtos.response;

import java.time.LocalDateTime;

public class AddressResponseDto {
    private Long id;
    private String street;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AddressResponseDto() {
    }

    public AddressResponseDto(Long id, String street, LocalDateTime createdAt) {
        this.id = id;
        this.street = street;
        this.createdAt = createdAt;
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
}
