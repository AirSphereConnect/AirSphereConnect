package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.FavoritesAlertsService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequestMapping("/api/alert/configurations")
public class FavoritesAlertsController {

    private final FavoritesAlertsService favoritesAlertsService;
    private final UserRepository userRepository;


    public FavoritesAlertsController(FavoritesAlertsService favoritesAlertsService, UserRepository userRepository) {
        this.favoritesAlertsService = favoritesAlertsService;
        this.userRepository = userRepository;
    }


    @PostMapping
    public FavoritesAlertsDto create(@RequestBody FavoritesAlertsDto dto, @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé"));

        return favoritesAlertsService.createAlertConfig(user.getId(),dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<FavoritesAlertsDto> getAllFavoritesAlerts() {
        return favoritesAlertsService.getAllFavoritesAlerts();
    }

    @GetMapping("/user/{userId}")
    public List<FavoritesAlertsDto> getUserAlerts(@PathVariable Long userId) {
        return favoritesAlertsService.getUserAlerts(userId);
    }

    @PutMapping("/{id}")
    public FavoritesAlertsDto update(@PathVariable Long id,@RequestBody FavoritesAlertsDto dto, @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé"));

        return favoritesAlertsService.updateAlertConfig(dto, user.getId(), id);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        favoritesAlertsService.deleteAlertConfig(id);
    }
}

