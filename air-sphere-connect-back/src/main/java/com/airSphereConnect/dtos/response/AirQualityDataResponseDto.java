package com.airSphereConnect.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

public class AirQualityDataResponseDto {

    private Long cityId;
    private String cityName;
    private String postalCode;
    private String areaCode;

    private Integer qualityIndex;
    private String qualityLabel;
    private String qualityColor;
    private LocalDateTime indexMeasuredAt;
    private String alertMessage;

    private Double pm10;
    private Double pm25;
    private Double no2;
    private Double o3;
    private Double so2;
    private LocalDateTime pollutantsMeasuredAt;

    private AirQualityMeasurementResponseDto latestMeasurement;
    private AirQualityIndexResponseDto latestIndex;
    private List<AirQualityMeasurementResponseDto> measurementHistory;
    private List<AirQualityIndexResponseDto> indexHistory;

    public AirQualityDataResponseDto() {}

    // Getters et Setters
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getQualityIndex() {
        return qualityIndex;
    }

    public void setQualityIndex(Integer qualityIndex) {
        this.qualityIndex = qualityIndex;
    }

    public String getQualityLabel() {
        return qualityLabel;
    }

    public void setQualityLabel(String qualityLabel) {
        this.qualityLabel = qualityLabel;
    }

    public String getQualityColor() {
        return qualityColor;
    }

    public void setQualityColor(String qualityColor) {
        this.qualityColor = qualityColor;
    }

    public LocalDateTime getIndexMeasuredAt() {
        return indexMeasuredAt;
    }

    public void setIndexMeasuredAt(LocalDateTime indexMeasuredAt) {
        this.indexMeasuredAt = indexMeasuredAt;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public Double getPm10() {
        return pm10;
    }

    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    public Double getPm25() {
        return pm25;
    }

    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    public Double getNo2() {
        return no2;
    }

    public void setNo2(Double no2) {
        this.no2 = no2;
    }

    public Double getO3() {
        return o3;
    }

    public void setO3(Double o3) {
        this.o3 = o3;
    }

    public Double getSo2() {
        return so2;
    }

    public void setSo2(Double so2) {
        this.so2 = so2;
    }

    public LocalDateTime getPollutantsMeasuredAt() {
        return pollutantsMeasuredAt;
    }

    public void setPollutantsMeasuredAt(LocalDateTime pollutantsMeasuredAt) {
        this.pollutantsMeasuredAt = pollutantsMeasuredAt;
    }

    public AirQualityMeasurementResponseDto getLatestMeasurement() {
        return latestMeasurement;
    }

    public void setLatestMeasurement(AirQualityMeasurementResponseDto latestMeasurement) {
        this.latestMeasurement = latestMeasurement;
    }

    public AirQualityIndexResponseDto getLatestIndex() {
        return latestIndex;
    }

    public void setLatestIndex(AirQualityIndexResponseDto latestIndex) {
        this.latestIndex = latestIndex;
    }

    public List<AirQualityMeasurementResponseDto> getMeasurementHistory() {
        return measurementHistory;
    }

    public void setMeasurementHistory(List<AirQualityMeasurementResponseDto> measurementHistory) {
        this.measurementHistory = measurementHistory;
    }

    public List<AirQualityIndexResponseDto> getIndexHistory() {
        return indexHistory;
    }

    public void setIndexHistory(List<AirQualityIndexResponseDto> indexHistory) {
        this.indexHistory = indexHistory;
    }
}