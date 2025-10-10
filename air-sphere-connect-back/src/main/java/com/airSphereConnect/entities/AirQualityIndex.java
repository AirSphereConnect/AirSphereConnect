package com.airSphereConnect.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "air_quality_index")
public class AirQualityIndex extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quality_index")
    private Integer qualityIndex;

    @Column(name = "quality_label", length = 20)
    private String qualityLabel;

    @Column(name = "quality_color", length = 20)
    private String qualityColor;


    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "area_code")
    private Integer areaCode;

    @Column(name = "area_name", length = 150)
    private String areaName;

    @Column(name = "alert_message", columnDefinition = "TEXT")
    private String alertMessage;

    @Column(name = "alert")
    private boolean alert = false;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    public AirQualityIndex() {

    }

    public AirQualityIndex(Integer qualityIndex, String qualityLabel, String qualityColor, String source, Integer areaCode, String areaName, String alertMessage, boolean alert, LocalDateTime measuredAt) {
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

    public Integer getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Integer areaCode) {
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
