package com.airsphereconnect.services;

import com.airsphereconnect.dtos.AlertsDto;

import java.util.List;

public interface AlertsService {
    void sendAlerts(AlertsDto dto);
    List<AlertsDto> getUserAlerts(Long userId);
}
