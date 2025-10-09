package com.airSphereConnect.controllers;

import com.airSphereConnect.services.api.WeatherSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//TODO Class à supprimer ou à garder pour faire appelle à des données historiques
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private WeatherSyncService weatherService;

    @PostMapping("/refresh-weather")
    public ResponseEntity<String> refreshWeatherNow() {
        weatherService.fetchAndStoreWeatherForAllCities();
        return ResponseEntity.ok("Météo mise à jour manuellement");
    }
}

