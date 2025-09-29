package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.services.FavoritesAlertsService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert/configurations")
public class FavoritesAlertsController {

    private final FavoritesAlertsService favoritesAlertsService;

    public FavoritesAlertsController(FavoritesAlertsService favoritesAlertsService) {
        this.favoritesAlertsService = favoritesAlertsService;
    }


    @PostMapping
    public FavoritesAlertsDto create(@RequestBody FavoritesAlertsDto dto) {
        return favoritesAlertsService.createAlertConfig(dto);
    }

    @GetMapping
//    @PostAuthorize("hasRole('ADMIN')")
    public List<FavoritesAlertsDto> getAllFavoritesAlerts() {
        return favoritesAlertsService.getAllFavoritesAlerts();
    }

    @GetMapping("/user/{userId}")
    public List<FavoritesAlertsDto> getUserAlerts(@PathVariable Long userId) {
        return favoritesAlertsService.getUserAlerts(userId);
    }

    @PutMapping
    public FavoritesAlertsDto update(@RequestBody FavoritesAlertsDto dto, @RequestParam Long userId) {
        return favoritesAlertsService.updateAlertConfig(dto, userId);
    }

    @PatchMapping("/{id}/enabled")
    public void setEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        favoritesAlertsService.setAlertEnabled(id, enabled);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        favoritesAlertsService.deleteAlertConfig(id);
    }
}

