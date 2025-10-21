package com.airSphereConnect.services;

import com.airSphereConnect.controllers.HomeController;
import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final HomeController homeController;

    public AuthService(UserService userService, HomeController homeController) {
        this.userService = userService;
        this.homeController = homeController;
    }

    public ResponseEntity<?> signupAndLogin(UserRequestDto dto, HttpServletResponse response) {
        // Crée l’utilisateur
        User created = userService.createUser(UserMapper.toEntity(dto));

        LoginRequestDto loginDto = new LoginRequestDto(created.getUsername(), dto.getPassword());
        return homeController.login(loginDto, response);
    }
}


