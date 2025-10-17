package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.services.security.ActiveTokenService;
import com.airSphereConnect.services.security.JwtService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.airSphereConnect.services.security.implementations.JwtServiceImpl.ACCESS_TOKEN_VALIDITY;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtServiceImpl;
    private final ActiveTokenService activeTokenService;
    private final UserMapper userMapper;

    public HomeController(JwtService jwtService, AuthenticationManager authenticationManager, JwtServiceImpl jwtServiceImpl, ActiveTokenService activeTokenService, UserMapper userMapper) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.jwtServiceImpl = jwtServiceImpl;
        this.activeTokenService = activeTokenService;
        this.userMapper = userMapper;
    }
    @GetMapping("/guest-token")
    public  ResponseEntity<Map<String, Object>> generateGuestToken(HttpServletResponse response) throws IOException {

        String guestToken = jwtService.generateGuestToken();

        Cookie cookie = new Cookie("ACCESS_TOKEN", guestToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // en production
        cookie.setPath("/");
        cookie.setMaxAge(jwtService.getAccessTokenExpirySeconds());
        response.addCookie(cookie);

        // Rediriger vers /api/home
        Map<String, Object> body = new HashMap<>();
        body.put("role", "GUEST");
        return ResponseEntity.ok(body);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @GetMapping("/home")
    public ResponseEntity<?> getHomePage() {
        return ResponseEntity.ok("Bienvenue sur la meilleur application");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        User userEntity = (User) auth.getPrincipal();

        String accessToken = jwtServiceImpl.generateToken(userEntity, ACCESS_TOKEN_VALIDITY);
        String refreshToken = jwtServiceImpl.generateRefreshToken(userEntity);
        activeTokenService.saveRefreshToken(userEntity.getUsername(), refreshToken);

        // Supprime ancien cookie ACCESS_TOKEN
        Cookie cleanCookie = new Cookie("ACCESS_TOKEN", null);
        cleanCookie.setHttpOnly(true);
        cleanCookie.setPath("/");
        cleanCookie.setMaxAge(0);
        response.addCookie(cleanCookie);

        // Ajoute nouveau cookie ACCESS_TOKEN
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(jwtServiceImpl.getAccessTokenExpirySeconds());
        response.addCookie(accessCookie);

        // Log cookie
        System.out.println("ACCESS_TOKEN cookie set: " + accessCookie.getName() + "=" + accessCookie.getValue());

        // Cookie refresh token
        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/api/users/refresh-token");
        refreshCookie.setMaxAge(jwtServiceImpl.getRefreshTokenExpirySeconds());
        response.addCookie(refreshCookie);
        System.out.println("REFRESH_TOKEN cookie set: " + refreshCookie.getName() + "=" + refreshCookie.getValue());

        UserResponseDto userResponse = userMapper.toDto(userEntity);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Connexion réussie");
        body.put("role", userEntity.getRole().name());
        body.put("user", userResponse);

        return ResponseEntity.ok(body);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        // Supprimer cookie JWT utilisateur
        Cookie deleteCookie = new Cookie("ACCESS_TOKEN", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setPath("/");
        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);

        // Créer cookie invité
        String guestToken = jwtService.generateGuestToken();
        Cookie guestCookie = new Cookie("ACCESS_TOKEN", guestToken);
        guestCookie.setHttpOnly(true);
        guestCookie.setPath("/");
        guestCookie.setMaxAge(jwtService.getAccessTokenExpirySeconds());
        response.addCookie(guestCookie);

        return ResponseEntity.ok().build();
    }



    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        String jwt = null;
        boolean isGuest = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null || jwt.isEmpty()) {
            // Pas de cookie, créer un cookie guest
            String guestToken = jwtService.generateGuestToken();

            Cookie guestCookie = new Cookie("ACCESS_TOKEN", guestToken);
            guestCookie.setHttpOnly(true);
            guestCookie.setPath("/");
            guestCookie.setMaxAge(jwtService.getAccessTokenExpirySeconds());

            response.addCookie(guestCookie);

            return ResponseEntity.ok(Map.of(
                    "role", "GUEST",
                    "user", null  // ou objet guest
            ));
        }

        try {
            UserDetails userDetails = jwtServiceImpl.extractUserDetails(jwt);
            if (!jwtServiceImpl.validateToken(jwt, userDetails)) {
                // Token invalide, créer cookie guest
                String guestToken = jwtService.generateGuestToken();

                Cookie guestCookie = new Cookie("ACCESS_TOKEN", guestToken);
                guestCookie.setHttpOnly(true);
                guestCookie.setPath("/");
                guestCookie.setMaxAge(jwtService.getAccessTokenExpirySeconds());

                response.addCookie(guestCookie);

                return ResponseEntity.ok(Map.of(
                        "role", "GUEST",
                        "user", null
                ));
            }

            // Token valide, regenérer token avec nouveau expire pour le même user
            String refreshedToken = jwtServiceImpl.generateToken(userDetails, JwtServiceImpl.ACCESS_TOKEN_VALIDITY);

            Cookie refreshedCookie = new Cookie("ACCESS_TOKEN", refreshedToken);
            refreshedCookie.setHttpOnly(true);
            refreshedCookie.setPath("/");
            refreshedCookie.setMaxAge(jwtService.getAccessTokenExpirySeconds());

            response.addCookie(refreshedCookie);

            // Retourne profil utilisateur
            User userEntity = (User) userDetails;
            UserResponseDto userResponseDto = userMapper.toDto(userEntity);

            return ResponseEntity.ok(Map.of(
                    "role", userEntity.getRole().name(),
                    "user", userResponseDto
            ));
        } catch (Exception e) {
            // Erreur extraction token, crée token guest
            String guestToken = jwtService.generateGuestToken();

            Cookie guestCookie = new Cookie("ACCESS_TOKEN", guestToken);
            guestCookie.setHttpOnly(true);
            guestCookie.setPath("/");
            guestCookie.setMaxAge(jwtService.getAccessTokenExpirySeconds());

            response.addCookie(guestCookie);

            return ResponseEntity.ok(Map.of(
                    "role", "GUEST",
                    "user", null
            ));
        }
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
