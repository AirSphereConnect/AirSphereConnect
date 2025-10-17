package com.airSphereConnect.dtos.response;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.Favorite;
import com.airSphereConnect.entities.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AddressResponseDto address;
    private List<FavoriteDto> favorites;


    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String username, String email, UserRole role, LocalDateTime createdAt, AddressResponseDto address, List<FavoriteDto> favorites) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.address = address;
        this.favorites = favorites;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
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

    public AddressResponseDto getAddress() {
        return address;
    }

    public void setAddress(AddressResponseDto address) {
        this.address = address;
    }

    public List<FavoriteDto> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<FavoriteDto> favorites) {
        this.favorites = favorites;
    }

}
