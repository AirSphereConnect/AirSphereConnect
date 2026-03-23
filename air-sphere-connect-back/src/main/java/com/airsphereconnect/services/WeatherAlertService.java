package com.airsphereconnect.services;

import com.airsphereconnect.dtos.ExternalAlertDto;
import com.airsphereconnect.entities.WeatherMeasurement;
import com.airsphereconnect.repositories.WeatherRepository;
import com.airsphereconnect.services.api.DataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherAlertService implements DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherAlertService.class);

    private static final boolean ENABLED = true;
    private static final Duration SYNC_INTERVAL = Duration.ofHours(24);
    private LocalDateTime lastSync = null;
    private int consecutiveErrors = 0;

    private final WeatherRepository weatherRepository;
    private final ExternalAlertProcessingService externalAlertProcessingService;

    public WeatherAlertService(WeatherRepository weatherRepository,
                               ExternalAlertProcessingService externalAlertProcessingService) {
        this.weatherRepository = weatherRepository;
        this.externalAlertProcessingService = externalAlertProcessingService;
    }

    @Override
    public String getServiceName() {
        return "WeatherSyncService";
    }

    @Override
    public boolean isEnabled() {
        return ENABLED;
    }

    @Override
    public Duration getSyncInterval() {
        return SYNC_INTERVAL;
    }

    @Override
    public LocalDateTime getLastSync() {
        return lastSync;
    }

    @Override
    public int getConsecutiveErrors() {
        return consecutiveErrors;
    }

    @Override
    public void syncData() {
        if (!ENABLED) {
            logger.info("Service {} désactivé, aucun appel de synchronisation.", getServiceName());
            return;
        }

        try {
            checkAndProcessAlertsForToday();
            lastSync = LocalDateTime.now();
            consecutiveErrors = 0;
            logger.info("Service {} : synchronisation réussie", getServiceName());
        } catch (Exception e) {
            consecutiveErrors++;
            logger.error("Service {} : erreur lors de la synchronisation : {}", getServiceName(), e.getMessage(), e);
        }
    }

    public void checkAndProcessAlertsForToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<WeatherMeasurement> alertsToday = weatherRepository.findByMeasuredAtBetweenAndAlertTrue(startOfDay, endOfDay);

        for (WeatherMeasurement wm : alertsToday) {
            ExternalAlertDto alertDto = new ExternalAlertDto();
            alertDto.setCityId(wm.getCity().getId());
            alertDto.setMessage(wm.getAlertMessage());
            alertDto.setType("WEATHER");

            externalAlertProcessingService.processExternalAlert(alertDto);
        }
    }
}
