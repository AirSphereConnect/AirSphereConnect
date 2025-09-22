package com.airSphereConnect.dtos;

import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.entities.enums.AlertType;

import java.time.LocalDateTime;

public class AlertsDto {
    private Long id;
    private Long userId;
    private Long cityId;

    private Long departmentId;
    private Long regionId;
    private String message;
    private AlertType alertType;
    private LocalDateTime sentAt;

    public AlertsDto() {}

    public AlertsDto(Long userId, Long cityId, AlertType alertType, String message, LocalDateTime sentAt, Long regionId, Long departmentId) {
        this.userId = userId;
        this.cityId = cityId;
        this.regionId = regionId;
        this.departmentId = departmentId;
        this.alertType = alertType;
        this.message = message;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
