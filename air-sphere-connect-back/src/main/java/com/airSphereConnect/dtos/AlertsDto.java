package com.airSphereConnect.dtos;

import com.airSphereConnect.dtos.response.WeatherAlertDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.entities.enums.AlertType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

/**
 * Data Transfer Object représentant une alerte personnalisée d’un utilisateur
 * liée à une ville, un département ou une région, avec type et message JSON.
 */
public class AlertsDto {
    private Long id;
    private Long userId;
    private City city;
    private Department department;
    private Region region;
    private String message;
    private AlertType alertType;
    private LocalDateTime sentAt;

    public AlertsDto() {}

    public AlertsDto(Long userId, City city, AlertType alertType, String message, LocalDateTime sentAt, Region region, Department department) {
        this.userId = userId;
        this.city = city;
        this.region = region;
        this.department = department;
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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartmentId(Department department) {
        this.department = department;
    }

    /**
     * Tente de parser le champ message JSON en objet WeatherAlertDto.
     *
     * @return objet WeatherAlertDto parsé, ou null si erreur
     */
    public WeatherAlertDto getParsedMessage() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(this.message, WeatherAlertDto.class);
        } catch (Exception e) {
            // TODO: Ajouter un logger pour enregistrer les erreurs de parsing
            return null;
        }
    }
}
