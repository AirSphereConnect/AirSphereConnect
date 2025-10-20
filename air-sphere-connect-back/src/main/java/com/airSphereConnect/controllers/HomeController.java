package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.services.security.ActiveTokenService;
import com.airSphereConnect.services.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ActiveTokenService activeTokenService;
    private final UserMapper userMapper;

    // Le flag secure dépend de l'environnement : vrai en prod HTTPS, faux en dev HTTP
    private final boolean cookieSecure = false; // passez à true en prod

    public HomeController(JwtService jwtService,
                          AuthenticationManager authenticationManager,
                          ActiveTokenService activeTokenService,
                          UserMapper userMapper) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.activeTokenService = activeTokenService;
        this.userMapper = userMapper;
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        // log cookie
        System.out.println("Set Cookie : " + name + " = " + value + ")");
        return cookie;
    }

    private Cookie deleteCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0); // expire immédiatement
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        return cookie;
    }


    @NotNull
    private ResponseEntity<?> getResponseEntity(HttpServletResponse response) {
        String guestToken = jwtService.generateGuestToken();
        response.addCookie(createCookie("ACCESS_TOKEN", guestToken));

        Map<String, Object> body = new HashMap<>();
        body.put("role", "GUEST");
        body.put("user", null);

        return ResponseEntity.ok(body);
    }

    @GetMapping("/guest-token")
    public ResponseEntity<Map<String, Object>> generateGuestToken(HttpServletResponse response) throws IOException {

        String guestToken = jwtService.generateGuestToken();

        Cookie cookie = createCookie("ACCESS_TOKEN", guestToken);
        System.out.println("ACCESS_TOKEN GUEST :" + guestToken);
        response.addCookie(cookie);


        Map<String, Object> body = new HashMap<>();
        body.put("role", "GUEST");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        User userEntity = (User) auth.getPrincipal();

        String accessToken = jwtService.generateToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);
        activeTokenService.saveRefreshToken(userEntity.getUsername(), refreshToken);

        // NE PAS supprimer explicitement l'ancien cookie, posez juste le nouveau
        Cookie accessCookie = createCookie("ACCESS_TOKEN", accessToken);
        response.addCookie(accessCookie);

        Cookie refreshCookie = createCookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setPath("/api/token/refresh");
        response.addCookie(refreshCookie);

        UserResponseDto userResponse = userMapper.toDto(userEntity);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Connexion réussie");
        body.put("role", userEntity.getRole().name());
        body.put("user", userResponse);

        return ResponseEntity.ok(body);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request, HttpServletResponse response) {
        // Headers cache control (bons pratiques)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        Cookie[] cookies = request.getCookies();
        String jwt = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null || jwt.isEmpty()) {
            // Pas de token => renvoyer token guest
            return getResponseEntity(response);
        }

        try {
            UserDetails userDetails = jwtService.extractUserDetails(jwt);
            if (!jwtService.validateToken(jwt, userDetails)) {
                // Jeton invalide => token guest
                return getResponseEntity(response);
            }

            // Ne pas réécrire le cookie guest ici si token valide
            User userEntity = (User) userDetails;
            UserResponseDto userResponseDto = userMapper.toDto(userEntity);

            Map<String, Object> body = new HashMap<>();
            body.put("role", userEntity.getRole() != null ? userEntity.getRole().name() : "GUEST");
            body.put("user", userResponseDto);

            return ResponseEntity.ok(body);
        } catch (Exception e) {
            // En cas d'erreur, renvoyer token guest
            return getResponseEntity(response);
        }
    }



    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        response.addCookie(deleteCookie("ACCESS_TOKEN"));

        String guestToken = jwtService.generateGuestToken();
        Cookie guestCookie = createCookie("ACCESS_TOKEN", guestToken);
        response.addCookie(guestCookie);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        return ResponseEntity.ok().build();
    }

}
