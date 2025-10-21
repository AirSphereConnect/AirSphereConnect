package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.services.security.ActiveTokenService;
import com.airSphereConnect.services.security.CookieService;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeController {

    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ActiveTokenService activeTokenService;
    private final CookieService cookieService;

    public HomeController(JwtService jwtService,
                          AuthenticationManager authenticationManager,
                          ActiveTokenService activeTokenService,
                          UserMapper userMapper,
                          CookieService cookieService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.activeTokenService = activeTokenService;
        this.userMapper = userMapper;
        this.cookieService = cookieService;
    }

    @NotNull
    private ResponseEntity<?> getGuestResponse(HttpServletResponse response) {
        String guestToken = jwtService.generateGuestToken();
        response.addCookie(cookieService.createCookie("ACCESS_TOKEN", guestToken));

        Map<String, Object> body = new HashMap<>();
        body.put("role", "GUEST");
        body.put("user", null);

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

        response.addCookie(cookieService.createCookie("ACCESS_TOKEN", accessToken));
        Cookie refreshCookie = cookieService.createCookie("REFRESH_TOKEN", refreshToken);
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
        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null || jwt.isEmpty()) {
            return getGuestResponse(response);
        }

        try {
            UserDetails userDetails = jwtService.extractUserDetails(jwt);
            if (!jwtService.validateToken(jwt, userDetails)) {
                return getGuestResponse(response);
            }

            User userEntity = (User) userDetails;
            UserResponseDto userResponseDto = userMapper.toDto(userEntity);

            Map<String, Object> body = new HashMap<>();
            body.put("role", userEntity.getRole() != null ? userEntity.getRole().name() : "GUEST");
            body.put("user", userResponseDto);

            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return getGuestResponse(response);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        response.addCookie(cookieService.deleteCookie("ACCESS_TOKEN"));
        response.addCookie(cookieService.createCookie("ACCESS_TOKEN", jwtService.generateGuestToken()));

        return ResponseEntity.ok().build();
    }
}
