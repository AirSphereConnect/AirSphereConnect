package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.ExternalAlertDto;
import com.airSphereConnect.services.ExternalAlertProcessingService;
import com.airSphereConnect.services.WeatherAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/external-alerts")
public class ExternalAlertController {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ExternalAlertController.class);
    private final ExternalAlertProcessingService processingService;

    public ExternalAlertController(ExternalAlertProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping("/receive")
    public void receiveAlert(@RequestBody ExternalAlertDto alert) {
        logger.debug("Réception alerte externe : cityId={}, type={}, message={}", alert.getCityId(), alert.getType(), alert.getMessage());
        processingService.processExternalAlert(alert);
    }
}

