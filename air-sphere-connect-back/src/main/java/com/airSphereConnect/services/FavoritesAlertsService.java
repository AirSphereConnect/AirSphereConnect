package com.airSphereConnect.services;

import com.airSphereConnect.dtos.FavoritesAlertsDto;

import java.util.List;

public interface FavoritesAlertsService {
    FavoritesAlertsDto createAlertConfig(FavoritesAlertsDto dto);
    List<FavoritesAlertsDto> getUserAlerts(Long userId);
    FavoritesAlertsDto updateAlertConfig(FavoritesAlertsDto dto);
    void setAlertEnabled(Long alertConfigId, boolean enabled);
    void deleteAlertConfig(Long alertConfigId);
}
