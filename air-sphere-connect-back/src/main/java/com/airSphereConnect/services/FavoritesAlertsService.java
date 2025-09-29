package com.airSphereConnect.services;

import com.airSphereConnect.dtos.FavoritesAlertsDto;

import java.util.List;

public interface FavoritesAlertsService {
    List<FavoritesAlertsDto> getAllFavoritesAlerts();
    FavoritesAlertsDto createAlertConfig(FavoritesAlertsDto dto);
    List<FavoritesAlertsDto> getUserAlerts(Long userId);
    FavoritesAlertsDto updateAlertConfig(FavoritesAlertsDto dto, Long userId);
    void setAlertEnabled(Long alertConfigId, boolean enabled);
    void deleteAlertConfig(Long alertConfigId);
}
