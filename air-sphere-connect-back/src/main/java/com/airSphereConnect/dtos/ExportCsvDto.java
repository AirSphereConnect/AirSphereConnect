package com.airSphereConnect.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExportCsvDto {

    private LocalDate dateMesureMeteo;
    private String nomVille;
    private Double latitude;
    private Double longitude;
    private Integer population;
    private Double temperature;
    private Double humidite;
    private Double pression;
    private Double vitesseVent;
    private Double directionVent;
    private String message;
    private Long stationId;
    private Double pm25;
    private Double pm10;
    private Double no2;
    private Double o3;
    private String unite;
    private Integer qualiteIndex;
    private String qualiteLabel;

    public ExportCsvDto(LocalDate dateMesureMeteo, String nomVille, Double latitude, Double longitude, Integer population, Double temperature, Double humidite, Double pression, Double vitesseVent, Double directionVent, String message, Long stationId, Double pm25, Double pm10, Double no2, Double o3, String unite, Integer qualiteIndex, String qualiteLabel) {
        this.dateMesureMeteo = dateMesureMeteo;
        this.nomVille = nomVille;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
        this.temperature = temperature;
        this.humidite = humidite;
        this.pression = pression;
        this.vitesseVent = vitesseVent;
        this.directionVent = directionVent;
        this.message = message;
        this.stationId = stationId;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.no2 = no2;
        this.o3 = o3;
        this.unite = unite;
        this.qualiteIndex = qualiteIndex;
        this.qualiteLabel = qualiteLabel;
    }

    public LocalDate getDateMesureMeteo() {
        return dateMesureMeteo;
    }

    public String getNomVille() {
        return nomVille;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getPopulation() {
        return population;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getHumidite() {
        return humidite;
    }

    public Double getPression() {
        return pression;
    }

    public Double getVitesseVent() {
        return vitesseVent;
    }

    public Double getDirectionVent() {
        return directionVent;
    }

    public String getMessage() {
        return message;
    }

    public Long getStationId() {
        return stationId;
    }

    public Double getPm25() {
        return pm25;
    }

    public Double getPm10() {
        return pm10;
    }

    public Double getNo2() {
        return no2;
    }

    public Double getO3() {
        return o3;
    }

    public String getUnite() {
        return unite;
    }

    public Integer getQualiteIndex() {
        return qualiteIndex;
    }

    public String getQualiteLabel() {
        return qualiteLabel;
    }
}

