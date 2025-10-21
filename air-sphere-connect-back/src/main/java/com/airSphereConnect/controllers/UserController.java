package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.services.AuthService;
import com.airSphereConnect.services.UserService;
import com.airSphereConnect.services.security.ActiveTokenService;
import com.airSphereConnect.services.security.JwtService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    // Tous les utilisateurs
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        boolean usernameTaken = username != null && userService.existsByUsername(username);
        boolean emailTaken = email != null && userService.existsByEmail(email);

        return ResponseEntity.ok(Map.of(
                "usernameTaken", usernameTaken,
                "emailTaken", emailTaken
        ));
    }


    // Par nom d'utilisateur
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/name")
    public UserResponseDto getUserByUsername(@RequestParam String username) {
        return UserMapper.toDto(userService.getUserByUsername(username));
    }

    // Par Id utilisateur
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id")
    public UserResponseDto getUserById(@RequestParam Long id) {
        return UserMapper.toDto(userService.getUserById(id));
    }

    // Création d'un utilisateur
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestDto reqDto, HttpServletResponse response) {
        return authService.signupAndLogin(reqDto, response);
    }



    // Mettre à jour un utilisateur
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @RequestBody UserRequestDto reqDto) {
        User user = UserMapper.toEntity(reqDto);
        User updated = userService.updateUser(id, user);
        return UserMapper.toDto(updated);
    }

    // Supprimer un utilisateur
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public UserResponseDto deleteUser(@PathVariable Long id) {
        User deletedUser = userService.deleteUser(id);
        return UserMapper.toDto(deletedUser);
    }
}
