package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.services.AlertsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class AlertsController {

    private final AlertsService alertsService;

    public AlertsController(AlertsService alertsService) {

        this.alertsService = alertsService;
    }

    @PostMapping("/send")
    public void sendAlerts(@RequestBody AlertsDto dto) {
        alertsService.sendAlerts(dto);
    }

    @GetMapping("/user/{userId}")
    public List<AlertsDto> getUserAlertss(@PathVariable Long userId) {
        return alertsService.getUserAlerts(userId);
    }
}

