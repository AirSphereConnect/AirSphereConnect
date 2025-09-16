package com.airSphereConnect.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "air_quality_measurements")
public class AirQualityMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pm10")
    private Double pm10;

    @Column(name = "pm25")
    private Double pm25;

    @Column(name = "no2")
    private Double no2;

    @Column(name = "o3")
    private Double o3;

    @Column(name = "so2")
    private Double so2;

    @Column(name = "atmo")
    private Integer atmo;

    @Column(name = "message", length = 50)
    private String message;

    @Column(name = "source", length = 100)
    private String source;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private AirQualityStation station;

    public AirQualityMeasurement() {
    }

    public AirQualityMeasurement(AirQualityStation station, LocalDateTime measuredAt, String source) {
        this.station = station;
        this.measuredAt = measuredAt;
        this.source = source;
    }

    public AirQualityMeasurement(Double pm10, Double pm25, Double no2, Double o3, Double so2, Integer atmo, String message, String source) {
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.atmo = atmo;
        this.message = message;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getAtmo() {
        return atmo;
    }

    public void setAtmo(Integer atmo) {
        this.atmo = atmo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    public AirQualityStation getStation() {
        return station;
    }

    public void setStation(AirQualityStation station) {
        this.station = station;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AirQualityMeasurement that = (AirQualityMeasurement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AirQualityMeasurement{" +
                "id=" + id +
                ", pm10=" + pm10 +
                ", pm25=" + pm25 +
                ", no2=" + no2 +
                ", o3=" + o3 +
                ", so2=" + so2 +
                ", atmo=" + atmo +
                ", message='" + message + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}


