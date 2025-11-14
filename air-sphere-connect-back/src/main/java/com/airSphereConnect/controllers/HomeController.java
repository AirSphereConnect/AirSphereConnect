package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.services.AuthService;
import com.airSphereConnect.services.UserService;
import com.airSphereConnect.services.security.ActiveTokenService;
import com.airSphereConnect.services.security.CookieService;
import com.airSphereConnect.services.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class HomeController {

    private final AuthService authService;

    public HomeController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto, HttpServletResponse response) {
        return authService.login(loginDto, response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request, HttpServletResponse response) {
        return authService.getUserProfile(request, response);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }

}
