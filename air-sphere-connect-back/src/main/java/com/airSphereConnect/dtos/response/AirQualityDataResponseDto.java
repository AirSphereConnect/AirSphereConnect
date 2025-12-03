package com.airSphereConnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Données complètes de qualité de l'air pour une ville")
public class AirQualityDataResponseDto {

    @Schema(description = "Identifiant de la ville", example = "1")
    private Long cityId;

    @Schema(description = "Nom de la ville", example = "Montpellier")
    private String cityName;

    @Schema(description = "Code postal", example = "34000")
    private String postalCode;

    @Schema(description = "Code de zone géographique", example = "243400017")
    private String areaCode;

    @Schema(description = "Indice de qualité de l'air (1-6)", example = "2")
    private Integer qualityIndex;

    @Schema(description = "Label de qualité", example = "Bon")
    private String qualityLabel;

    @Schema(description = "Couleur de l'indice", example = "#51CCAA")
    private String qualityColor;

    @Schema(description = "Date de mesure de l'indice")
    private LocalDateTime indexMeasuredAt;

    @Schema(description = "Message d'alerte éventuel")
    private String alertMessage;

    @Schema(description = "Concentration PM10 en µg/m³", example = "15.5")
    private Double pm10;

    @Schema(description = "Concentration PM2.5 en µg/m³", example = "8.3")
    private Double pm25;

    @Schema(description = "Concentration NO2 en µg/m³", example = "25.7")
    private Double no2;

    @Schema(description = "Concentration O3 en µg/m³", example = "45.2")
    private Double o3;

    @Schema(description = "Concentration SO2 en µg/m³", example = "5.1")
    private Double so2;

    @Schema(description = "Date de mesure des polluants")
    private LocalDateTime pollutantsMeasuredAt;

    @Schema(description = "Dernière mesure de polluants")
    private AirQualityMeasurementResponseDto latestMeasurement;

    @Schema(description = "Dernier indice de qualité")
    private AirQualityIndexResponseDto latestIndex;

    @Schema(description = "Historique des mesures de polluants")
    private List<AirQualityMeasurementResponseDto> measurementHistory;

    @Schema(description = "Historique des indices de qualité")
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