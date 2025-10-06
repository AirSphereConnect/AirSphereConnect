package com.airSphereConnect.entities;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "air_quality_measurements")
public class AirQualityMeasurement extends Timestamp {

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

    @Column(name = "unit", length = 10)
    private String unit;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private AirQualityStation station;

    public AirQualityMeasurement() {
    }

    public AirQualityMeasurement(Double pm10, Double pm25, Double no2, Double o3, Double so2, String unit, LocalDateTime measuredAt, AirQualityStation station) {
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.unit = unit;
        this.measuredAt = measuredAt;
        this.station = station;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
}


