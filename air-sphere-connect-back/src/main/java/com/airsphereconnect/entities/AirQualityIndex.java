package com.airsphereconnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité représentant l'indice de qualité de l'air pour une zone géographique.
 * Contient l'indice, le libellé, la couleur et les informations d'alerte associées.
 */
@Entity
@Table(name = "air_quality_index")
public class AirQualityIndex implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{airquality.qualityIndex.required}")
    @Column(name = "quality_index")
    private Integer qualityIndex;

    @NotBlank(message = "{airquality.qualityLabel.required}")
    @Size(max = 20, message = "{airquality.qualityLabel.size}")
    @Column(name = "quality_label", length = 20)
    private String qualityLabel;

    @Size(max = 7, message = "{airquality.qualityColor.size}")
    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "{airquality.qualityColor.pattern}"
    )
    @Column(name = "quality_color", length = 20)
    private String qualityColor;

    @NotBlank(message = "{airquality.source.required}")
    @Size(max = 50, message = "{airquality.source.size}")
    @Column(name = "source", length = 50)
    private String source;

    @NotNull(message = "{airquality.areaCode.required}")
    @Column(name = "area_code", nullable = false)
    private String areaCode;

    @NotBlank(message = "{airquality.areaName.required}")
    @Size(max = 150, message = "{airquality.areaName.size}")
    @Column(name = "area_name", length = 150)
    private String areaName;

    @Size(max = 500, message = "{airquality.alertMessage.size}")
    @Column(name = "alert_message", columnDefinition = "TEXT")
    private String alertMessage;

    @Column(name = "alert")
    private boolean alert = false;

    @NotNull(message = "{airquality.measuredAt.required}")
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @OneToMany(mappedBy = "airQualityIndex", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<City> cities = new ArrayList<>();

    /**
     * Constructeur par défaut.
     */
    public AirQualityIndex() {

    }

    /**
     * Constructeur avec tous les paramètres.
     *
     * @param qualityIndex  L'indice de qualité de l'air
     * @param qualityLabel  Le libellé de la qualité de l'air
     * @param qualityColor  La couleur associée à l'indice
     * @param source        La source des données
     * @param areaCode      Le code de la zone géographique
     * @param areaName      Le nom de la zone géographique
     * @param alertMessage  Le message d'alerte
     * @param alert         Indique si une alerte est active
     * @param measuredAt    La date et l'heure de la mesure
     */
    @SuppressWarnings("java:S107")
    public AirQualityIndex(Integer qualityIndex, String qualityLabel, String qualityColor, String source, String areaCode, String areaName, String alertMessage, boolean alert, LocalDateTime measuredAt) {
        this.qualityIndex = qualityIndex;
        this.qualityLabel = qualityLabel;
        this.qualityColor = qualityColor;
        this.source = source;
        this.areaCode = areaCode;
        this.areaName = areaName;
        this.alertMessage = alertMessage;
        this.alert = alert;
        this.measuredAt = measuredAt;
    }

    /**
     * Retourne l'identifiant unique de l'indice de qualité de l'air.
     *
     * @return L'identifiant unique
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'indice de qualité de l'air.
     *
     * @param id L'identifiant unique
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne l'indice de qualité de l'air.
     *
     * @return L'indice de qualité de l'air
     */
    public Integer getQualityIndex() {
        return qualityIndex;
    }

    /**
     * Définit l'indice de qualité de l'air.
     *
     * @param qualityIndex L'indice de qualité de l'air
     */
    public void setQualityIndex(Integer qualityIndex) {
        this.qualityIndex = qualityIndex;
    }

    /**
     * Retourne le libellé de la qualité de l'air.
     *
     * @return Le libellé de la qualité de l'air
     */
    public String getQualityLabel() {
        return qualityLabel;
    }

    /**
     * Définit le libellé de la qualité de l'air.
     *
     * @param qualityLabel Le libellé de la qualité de l'air
     */
    public void setQualityLabel(String qualityLabel) {
        this.qualityLabel = qualityLabel;
    }

    /**
     * Retourne la couleur associée à l'indice de qualité de l'air.
     *
     * @return La couleur au format hexadécimal
     */
    public String getQualityColor() {
        return qualityColor;
    }

    /**
     * Définit la couleur associée à l'indice de qualité de l'air.
     *
     * @param qualityColor La couleur au format hexadécimal
     */
    public void setQualityColor(String qualityColor) {
        this.qualityColor = qualityColor;
    }

    /**
     * Retourne la source des données de qualité de l'air.
     *
     * @return La source des données
     */
    public String getSource() {
        return source;
    }

    /**
     * Définit la source des données de qualité de l'air.
     *
     * @param source La source des données
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Retourne le code de la zone géographique.
     *
     * @return Le code de la zone
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Définit le code de la zone géographique.
     *
     * @param areaCode Le code de la zone
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * Retourne le nom de la zone géographique.
     *
     * @return Le nom de la zone
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * Définit le nom de la zone géographique.
     *
     * @param areaName Le nom de la zone
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
     * Retourne le message d'alerte associé à la qualité de l'air.
     *
     * @return Le message d'alerte
     */
    public String getAlertMessage() {
        return alertMessage;
    }

    /**
     * Définit le message d'alerte associé à la qualité de l'air.
     *
     * @param alertMessage Le message d'alerte
     */
    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    /**
     * Indique si une alerte est active pour cette mesure.
     *
     * @return true si une alerte est active, false sinon
     */
    public boolean getAlert() {
        return alert;
    }

    /**
     * Définit si une alerte est active pour cette mesure.
     *
     * @param alert true si une alerte est active, false sinon
     */
    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    /**
     * Retourne la liste des villes associées à cet indice de qualité de l'air.
     *
     * @return La liste des villes
     */
    public List<City> getCities() {
        return cities;
    }

    /**
     * Définit la liste des villes associées à cet indice de qualité de l'air.
     *
     * @param cities La liste des villes
     */
    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AirQualityIndex that = (AirQualityIndex) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}