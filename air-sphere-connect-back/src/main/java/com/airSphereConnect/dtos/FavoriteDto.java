package com.airSphereConnect.dtos;

import com.airSphereConnect.entities.enums.FavoriteCategory;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO représentant un favori utilisateur lié à une ville,
 * avec ses catégories, dates et nom de ville.
 */
public class FavoriteDto {

    private Long id;

    private FavoriteCategory favoriteCategory;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    private Long userId;

    private Long cityId;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    private String cityName;

    public FavoriteDto() {
    }

    public FavoriteDto(Long id, FavoriteCategory favoriteCategory, LocalDateTime createdAt,
                       LocalDateTime updatedAt, Long userId, Long cityId, String cityName) {
        this.id = id;
        this.favoriteCategory = favoriteCategory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.cityId = cityId;
        this.cityName = cityName;
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
