package com.airSphereConnect.services;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.dtos.ExternalAlertDto;
import com.airSphereConnect.entities.FavoritesAlerts;
import com.airSphereConnect.entities.enums.AlertType;
import com.airSphereConnect.repositories.FavoritesAlertsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class ExternalAlertProcessingService {
//    private static final Logger logger = (Logger) LoggerFactory.getLogger(ExternalAlertProcessingService.class);

    private final FavoritesAlertsRepository favoritesAlertsRepository;
    private final AlertsService alertsService;

    public ExternalAlertProcessingService(AlertsService alertsService, FavoritesAlertsRepository favoritesAlertsRepository) {
        this.favoritesAlertsRepository = favoritesAlertsRepository;
        this.alertsService = alertsService;
    }

    public void processExternalAlert(ExternalAlertDto alert) {
        // Cherche tous les favoris actifs pour city, department, region
//        logger.debug("Traitement de l'alerte externe pour la ville {}", alert.getCityId());
        List<FavoritesAlerts> configs;

        if (alert.getCityId() != null) {
            configs = favoritesAlertsRepository.findByCityIdAndIsEnabled(alert.getCityId(), true);
        } else if (alert.getDepartmentId() != null) {
            configs = favoritesAlertsRepository.findByDepartmentIdAndIsEnabled(alert.getDepartmentId(), true);
        } else if (alert.getRegionId() != null) {
            configs = favoritesAlertsRepository.findByRegionIdAndIsEnabled(alert.getRegionId(), true);
        } else {
//            logger.warn("Aucune zone géographique valide spécifiée dans l'alerte (cityId, departmentId, regionId sont tous nuls).");
            return; // Pas de traitement possible
        }
//        logger.debug("Nombre de favoris alertes trouvés : {}", configs.size());

        // Chaque favori, créé une alerte et notifie l'utilisateur par email
        for (FavoritesAlerts config : configs) {
//            logger.debug("Traitement config alerte favorite id={} pour user id={}", config.getId(), config.getUser().getId());
            AlertsDto dto = buildNotificationDTO(config, alert);
            alertsService.sendAlerts(dto); // <-- Doit persister et déclencher l'email
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

