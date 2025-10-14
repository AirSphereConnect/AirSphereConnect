package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.AirQualityStationResponseDto;
import com.airSphereConnect.services.AirQualityService;
import com.airSphereConnect.services.WeatherService;
import com.airSphereConnect.services.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GuestController {

    private final JwtService jwtService;

    public GuestController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/guest-token")
    public void generateGuestTokenAndRedirect(HttpServletResponse response) throws IOException {
        // Générer le token guest
        String guestToken = jwtService.generateGuestToken();

        // Créer le cookie
        Cookie cookie = new Cookie("ACCESS_TOKEN", guestToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // en production
        cookie.setPath("/");
        cookie.setMaxAge(jwtService.getAccessTokenExpirySeconds());
        response.addCookie(cookie);

        // Rediriger vers /api/air-quality/stations
        response.sendRedirect("/api/air-quality/stations");
    }
}
