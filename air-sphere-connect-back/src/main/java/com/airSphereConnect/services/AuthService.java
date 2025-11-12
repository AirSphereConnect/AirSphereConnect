package com.airSphereConnect.services;

import com.airSphereConnect.controllers.HomeController;
import com.airSphereConnect.dtos.request.LoginRequestDto;
import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public ResponseEntity<?> EditUserLogin(UserRequestDto reqDto, User currentUser,
                                           HttpServletRequest request, HttpServletResponse response) {

        User userToUpdate = UserMapper.toEntity(reqDto);
        User updatedUser = userService.updateUser(currentUser.getId(), userToUpdate);

        boolean usernameChanged = reqDto.getUsername() != null && !reqDto.getUsername().isEmpty();
        boolean passwordChanged = reqDto.getPassword() != null && !reqDto.getPassword().isEmpty();

        if (usernameChanged || passwordChanged) {
            // Invalide session + remplace token par "guest" (logout forcé)
            return homeController.logout(request, response);
        }

        // Sinon, retourne utilisateur mis à jour normalement
        return ResponseEntity.ok(UserMapper.toDto(updatedUser));
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public ResponseEntity<?> DeleteUser(Long id,
                                        HttpServletRequest request, HttpServletResponse response) {

        userService.deleteUser(id);

        return ResponseEntity.ok(null);
    }

}


