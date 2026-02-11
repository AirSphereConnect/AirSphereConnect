package com.airSphereConnect.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant une mesure de la qualité de l'air.
 * Stocke les valeurs de concentration des polluants mesurées à une station de qualité de l'air spécifique.
 */
@Entity
@Table(name = "air_quality_measurements")
public class AirQualityMeasurement {

    /**
     * Identifiant unique de la mesure.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Concentration de particules PM10 (particules < 10 micromètres).
     */
    @Column(name = "pm10")
    private Double pm10;

    /**
     * Concentration de particules PM2.5 (particules < 2.5 micromètres).
     */
    @Column(name = "pm25")
    private Double pm25;

    /**
     * Concentration de dioxyde d'azote (NO2).
     */
    @Column(name = "no2")
    private Double no2;

    /**
     * Concentration d'ozone (O3).
     */
    @Column(name = "o3")
    private Double o3;

    /**
     * Concentration de dioxyde de soufre (SO2).
     */
    @Column(name = "so2")
    private Double so2;

    /**
     * Unité de mesure des concentrations de polluants (ex: µg/m³).
     */
    @NotBlank(message = "{measurement.unit.required}")
    @Size(max = 10, message = "{measurement.unit.size}")
    @Column(name = "unit", length = 10)
    private String unit;

    /**
     * Date et heure de la mesure.
     */
    @NotNull(message = "{measurement.measuredAt.required}")
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    /**
     * Station de qualité de l'air où cette mesure a été enregistrée.
     */
    @NotNull(message = "{measurement.station.required}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private AirQualityStation station;

    /**
     * Constructeur par défaut.
     */
    public AirQualityMeasurement() {
    }

    /**
     * Construit une mesure de qualité de l'air avec toutes les valeurs de polluants.
     *
     * @param pm10 concentration PM10
     * @param pm25 concentration PM2.5
     * @param no2 concentration NO2
     * @param o3 concentration O3
     * @param so2 concentration SO2
     * @param unit unité de mesure
     * @param measuredAt horodatage de la mesure
     * @param station station de qualité de l'air
     */
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

    /**
     * Retourne l'identifiant unique de la mesure.
     *
     * @return L'identifiant unique
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de la mesure.
     *
     * @param id L'identifiant unique
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne la concentration de particules PM10.
     *
     * @return La concentration de PM10
     */
    public Double getPm10() {
        return pm10;
    }

    /**
     * Définit la concentration de particules PM10.
     *
     * @param pm10 La concentration de PM10
     */
    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    /**
     * Retourne la concentration de particules PM2.5.
     *
     * @return La concentration de PM2.5
     */
    public Double getPm25() {
        return pm25;
    }

    /**
     * Définit la concentration de particules PM2.5.
     *
     * @param pm25 La concentration de PM2.5
     */
    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    /**
     * Retourne la concentration de dioxyde d'azote (NO2).
     *
     * @return La concentration de NO2
     */
    public Double getNo2() {
        return no2;
    }

    /**
     * Définit la concentration de dioxyde d'azote (NO2).
     *
     * @param no2 La concentration de NO2
     */
    public void setNo2(Double no2) {
        this.no2 = no2;
    }

    /**
     * Retourne la concentration d'ozone (O3).
     *
     * @return La concentration d'O3
     */
    public Double getO3() {
        return o3;
    }

    /**
     * Définit la concentration d'ozone (O3).
     *
     * @param o3 La concentration d'O3
     */
    public void setO3(Double o3) {
        this.o3 = o3;
    }

    /**
     * Retourne la concentration de dioxyde de soufre (SO2).
     *
     * @return La concentration de SO2
     */
    public Double getSo2() {
        return so2;
    }

    /**
     * Définit la concentration de dioxyde de soufre (SO2).
     *
     * @param so2 La concentration de SO2
     */
    public void setSo2(Double so2) {
        this.so2 = so2;
    }

    /**
     * Retourne l'unité de mesure des concentrations.
     *
     * @return L'unité de mesure
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Définit l'unité de mesure des concentrations.
     *
     * @param unit L'unité de mesure
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Retourne la date et l'heure de la mesure.
     *
     * @return La date et l'heure de la mesure
     */
    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    /**
     * Définit la date et l'heure de la mesure.
     *
     * @param measuredAt La date et l'heure de la mesure
     */
    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }

    /**
     * Retourne la station de qualité de l'air associée à cette mesure.
     *
     * @return La station de qualité de l'air
     */
    public AirQualityStation getStation() {
        return station;
    }

    /**
     * Définit la station de qualité de l'air associée à cette mesure.
     *
     * @param station La station de qualité de l'air
     */
    public void setStation(AirQualityStation station) {
        this.station = station;
    }
}


