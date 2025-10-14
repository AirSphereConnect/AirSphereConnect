package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.services.security.ActiveTokenService;
import com.airSphereConnect.services.security.JwtService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtServiceImpl;
    private final ActiveTokenService activeTokenService;

    public HomeController(JwtService jwtService, AuthenticationManager authenticationManager, JwtServiceImpl jwtServiceImpl, ActiveTokenService activeTokenService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.jwtServiceImpl = jwtServiceImpl;
        this.activeTokenService = activeTokenService;
    }
    @GetMapping("/guest-token")
    public void generateGuestTokenAndRedirect(HttpServletResponse response) throws IOException {

        String guestToken = jwtService.generateGuestToken();

        Cookie cookie = new Cookie("ACCESS_TOKEN", guestToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // en production
        cookie.setPath("/");
        cookie.setMaxAge(jwtService.getAccessTokenExpirySeconds());
        response.addCookie(cookie);

        // Rediriger vers /api/home
        response.sendRedirect("/api/home");
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @GetMapping("/home")
    public ResponseEntity<?> getHomePage() {
        return ResponseEntity.ok("Bienvenue sur la meilleur application");
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto loginDto, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String accessToken = jwtServiceImpl.generateAccessToken(userDetails);
        String refreshToken = jwtServiceImpl.generateRefreshToken(userDetails);

        activeTokenService.saveRefreshToken(userDetails.getUsername(), refreshToken);

        Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(jwtServiceImpl.getAccessTokenExpirySeconds());
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/users/refresh-token");
        refreshCookie.setMaxAge(jwtServiceImpl.getRefreshTokenExpirySeconds());
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(Map.of("message", "Connexion réussie"));
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[0])
                .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token manquant");
        }

        UserDetails userDetails;
        try {
            userDetails = jwtServiceImpl.extractUserDetails(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Refresh token invalide");
        }

        if (!activeTokenService.isTokenActive(userDetails.getUsername(), refreshToken)) {
            return ResponseEntity.status(403).body("Refresh token non associé");
        }

        if (!jwtServiceImpl.validateToken(refreshToken, userDetails)) {
            return ResponseEntity.status(403).body("Refresh token expiré");
        }

        String newAccessToken = jwtServiceImpl.generateAccessToken(userDetails);

        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(jwtServiceImpl.getAccessTokenExpirySeconds());
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok(Map.of("message", "Token renouvelé"));
    }
}
