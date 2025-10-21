package com.airSphereConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "air_quality_index")
public class AirQualityIndex {
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
    @Column(name = "area_code", unique = true)
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

    @OneToMany(mappedBy = "airQualityIndex")
    private List<City> cities = new ArrayList<>();

    public AirQualityIndex() {

    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }
    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public boolean getAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
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