package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class WeatherResponseDto {

    private Long cityId;
    private String cityName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime measuredAt;

    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private Double windDirection;
    private Double pressure;
    private WeatherDescriptionDto[] message;
    private Boolean alert;
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

