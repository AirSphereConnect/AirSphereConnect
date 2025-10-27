package com.airSphereConnect.services;

import com.airSphereConnect.dtos.FavoritesAlertsDto;

import java.util.List;

public interface FavoritesAlertsService {
    List<FavoritesAlertsDto> getAllFavoritesAlerts();
    FavoritesAlertsDto createAlertConfig(Long id, FavoritesAlertsDto dto);
    List<FavoritesAlertsDto> getUserAlerts(Long userId);
    FavoritesAlertsDto updateAlertConfig(FavoritesAlertsDto dto, Long userId);
    void deleteAlertConfig(Long alertConfigId);
}
