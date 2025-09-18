package com.airSphereConnect.dtos;

import com.airSphereConnect.entities.enums.FavoriteCategory;

import java.time.LocalDateTime;

public class FavoriteDto {

    private Long id;
    private FavoriteCategory favoriteCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private Long cityId;

    public FavoriteDto() {}

    public FavoriteDto(Long id, FavoriteCategory favoriteCategory, LocalDateTime createdAt,
                       LocalDateTime updatedAt, Long userId, Long cityId) {
        this.id = id;
        this.favoriteCategory = favoriteCategory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.cityId = cityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FavoriteCategory getFavoriteCategory() {
        return favoriteCategory;
    }

    public void setFavoriteCategory(FavoriteCategory favoriteCategory) {
        this.favoriteCategory = favoriteCategory;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }
}
