package com.airSphereConnect.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiWeatherResponseDto {

    private Main main;
    private Wind wind;
    private Weather[] weather;
    private Alert[] alerts;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Alert[] getAlerts() {
        return alerts;
    }

    public void setAlerts(Alert[] alerts) {
        this.alerts = alerts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private Double temp;
        private Double humidity;
        private Double pressure;

        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        public Double getHumidity() {
            return humidity;
        }

        public void setHumidity(Double humidity) {
            this.humidity = humidity;
        }

        public Double getPressure() {
            return pressure;
        }

        public void setPressure(Double pressure) {
            this.pressure = pressure;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private Double speed;
        private Double deg;

        public Double getSpeed() {
            return speed;
        }
        public void setSpeed(Double speed) {
            this.speed = speed;
        }
        public Double getDeg() {
            return deg;
        }
        public void setDeg(Double deg) {
            this.deg = deg;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String main;
        private String description;
        private String icon;

        public String getMain() {
            return main;
        }
        public void setMain(String main) {
            this.main = main;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getIcon() {
            return icon;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Alert {
        private Boolean event;
        private String description;

        public Boolean getEvent() {
            return event;
        }

        public void setEvent(Boolean event) {
            this.event = event;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }




}
