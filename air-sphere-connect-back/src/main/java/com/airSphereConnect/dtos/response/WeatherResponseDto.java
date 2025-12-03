package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.LocalDateTime;

@Schema(description = "Réponse contenant les données météo pour une ville, ainsi que les alertes associées.")
public class WeatherResponseDto {

    @Schema(description = "Identifiant unique de la ville.", example = "12")
    private Long cityId;

    @Schema(description = "Nom de la ville.", example = "Paris")
    private String cityName;

    @Schema(description = "Date et heure de la mesure météorologique.", example = "2025-11-24T15:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime measuredAt;

    @Schema(description = "Température actuelle en degrés Celsius.", example = "18.3")
    private Double temperature;

    @Schema(description = "Humidité relative en pourcentage.", example = "60")
    private Double humidity;

    @Schema(description = "Vitesse du vent en m/s.", example = "5.5")
    private Double windSpeed;

    @Schema(description = "Direction du vent en degrés.", example = "180")
    private Double windDirection;

    @Schema(description = "Pression atmosphérique en hPa.", example = "1012")
    private Double pressure;

    @Schema(description = "Tableau des descriptions détaillées des conditions météo.")
    private WeatherDescriptionDto[] message;

    @Schema(description = "Indique si une alerte météo est active pour cette ville.", example = "true")
    private Boolean alert;

    @Schema(description = "Tableau des alertes météo actives pour cette ville.")
    private WeatherAlertDto[] alertMessage;


    public WeatherResponseDto(Long cityId, String cityName, LocalDateTime measuredAt, Double temperature, Double humidity, Double pressure, Double windSpeed, Double windDirection, WeatherDescriptionDto[] message, Boolean alert, WeatherAlertDto[] alertMessage) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.measuredAt = measuredAt;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.pressure = pressure;
        this.message = message;
        this.alert = alert;
        this.alertMessage = alertMessage;
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(Double windDirection) {
        this.windDirection = windDirection;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public WeatherDescriptionDto[] getMessage() {
        return message;
    }

    public void setMessage(WeatherDescriptionDto[] message) {
        this.message = message;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    public WeatherAlertDto[] getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(WeatherAlertDto[] alertMessage) {
        this.alertMessage = alertMessage;
    }

}

