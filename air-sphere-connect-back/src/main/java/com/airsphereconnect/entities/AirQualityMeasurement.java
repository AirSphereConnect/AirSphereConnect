package com.airsphereconnect.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité représentant une mesure de la qualité de l'air.
 * Stocke les valeurs de concentration des polluants mesurées à une station de qualité de l'air spécifique.
 */
@Entity
@Table(name = "air_quality_measurements")
public class AirQualityMeasurement implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // Identifiant unique de la mesure.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Concentration de particules PM10 (particules < 10 micromètres).
    @Column(name = "pm10")
    private Double pm10;
    // Concentration de particules PM2.5 (particules < 2.5 micromètres).
    @Column(name = "pm25")
    private Double pm25;
    // Concentration de dioxyde d'azote (NO2).
    @Column(name = "no2")
    private Double no2;
    // Concentration d'ozone (O3).
    @Column(name = "o3")
    private Double o3;
    // Concentration de dioxyde de soufre (SO2).
    @Column(name = "so2")
    private Double so2;
    // Unité de mesure des concentrations de polluants (ex: µg/m³).
    @NotBlank(message = "{measurement.unit.required}")
    @Size(max = 10, message = "{measurement.unit.size}")
    @Column(name = "unit", length = 10)
    private String unit;
    // Date et heure de la mesure.
    @NotNull(message = "{measurement.measuredAt.required}")
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;
    // Station de qualité de l'air où cette mesure a été enregistrée.
    @NotNull(message = "{measurement.station.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private AirQualityStation station;

    // Constructeur par défaut.
    public AirQualityMeasurement() {}
    /**
     * Construit une mesure de qualité de l'air avec toutes les valeurs de polluants.
     * @param pm10 concentration PM10
     * @param pm25 concentration PM2.5
     * @param no2 concentration NO2
     * @param o3 concentration O3
     * @param so2 concentration SO2
     * @param unit unité de mesure
     * @param measuredAt horodatage de la mesure
     * @param station station de qualité de l'air
     */
    @SuppressWarnings("java:S107")
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