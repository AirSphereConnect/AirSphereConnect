package com.airSphereConnect.dtos;

import java.time.LocalDateTime;

public class FavoritesAlertsDto {
    private Long id;
    private Long userId;
    private Long cityId;
    private Long departmentId;
    private Long regionId;
    private boolean isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FavoritesAlertsDto() {}

    public FavoritesAlertsDto(Long userId, Long cityId, Long departmentId, Long regionId, boolean isEnabled) {
        this.userId = userId;
        this.cityId = cityId;
        this.departmentId = departmentId;
        this.regionId = regionId;
        this.isEnabled = isEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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
