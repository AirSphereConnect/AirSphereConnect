package com.airsphereconnect.services;

import com.airsphereconnect.dtos.AlertsDto;
import com.airsphereconnect.dtos.ExternalAlertDto;
import com.airsphereconnect.entities.FavoritesAlerts;
import com.airsphereconnect.entities.enums.AlertType;
import com.airsphereconnect.repositories.FavoritesAlertsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExternalAlertProcessingService {

    private final FavoritesAlertsRepository favoritesAlertsRepository;
    private final AlertsService alertsService;

    public ExternalAlertProcessingService(AlertsService alertsService, FavoritesAlertsRepository favoritesAlertsRepository) {
        this.favoritesAlertsRepository = favoritesAlertsRepository;
        this.alertsService = alertsService;
    }

    public void processExternalAlert(ExternalAlertDto alert) {
        List<FavoritesAlerts> configs;

        if (alert.getCityId() != null) {
            configs = favoritesAlertsRepository.findByCityIdAndEnabled(alert.getCityId(), true);
        } else {
            return; // Aucune zone valide, pas de traitement voir pour ajout
        }

        for (FavoritesAlerts config : configs) {
            AlertsDto dto = buildNotificationDTO(config, alert);
            alertsService.sendAlerts(dto);
        }
    }

    private AlertsDto buildNotificationDTO(FavoritesAlerts config, ExternalAlertDto alert) {
        AlertsDto dto = new AlertsDto();
        dto.setUserId(config.getUser().getId());
        dto.setCity(config.getCity());
        dto.setDepartmentId(config.getDepartment());
        dto.setRegion(config.getRegion());
        dto.setAlertType(AlertType.valueOf(alert.getType().toUpperCase()));
        dto.setMessage(alert.getMessage());
        return dto;
    }
}
