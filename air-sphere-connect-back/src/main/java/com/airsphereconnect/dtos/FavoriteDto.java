package com.airsphereconnect.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO représentant un favori utilisateur lié à une ville,
 * avec ses catégories, dates et nom de ville.
 */
public class FavoriteDto {

    private Long id;
    private Boolean selectWeather;
    private Boolean selectAirQuality;
    private Boolean selectPopulation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    private Long userId;
    private Long cityId;
    private String cityName;

    public FavoriteDto() {
    }

    @SuppressWarnings("java:S107")
    public FavoriteDto(Long id, boolean selectPopulation, boolean selectAirQuality, boolean selectWeather, LocalDateTime createdAt,
                       LocalDateTime updatedAt, Long userId, Long cityId, String cityName) {
        this.id = id;
        this.selectPopulation = selectPopulation;
        this.selectAirQuality = selectAirQuality;
        this.selectWeather = selectWeather;
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


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Boolean getSelectWeather() {
        return selectWeather;
    }

    public void setSelectWeather(Boolean selectWeather) {
        this.selectWeather = selectWeather;
    }

    public Boolean getSelectAirQuality() {
        return selectAirQuality;
    }

    public void setSelectAirQuality(Boolean selectAirQuality) {
        this.selectAirQuality = selectAirQuality;
    }

    public Boolean getSelectPopulation() {
        return selectPopulation;
    }

    public void setSelectPopulation(Boolean selectPopulation) {
        this.selectPopulation = selectPopulation;
    }

}
