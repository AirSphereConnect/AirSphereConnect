package com.airSphereConnect.services;

import com.airSphereConnect.dtos.AlertsDto;

import java.util.List;

public interface AlertsService {
    void sendAlerts(AlertsDto dto);
    List<AlertsDto> getUserAlerts(Long userId);
}
