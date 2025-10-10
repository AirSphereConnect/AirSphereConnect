package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.services.UserService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//TODO Il faut ajouter l'adresse aux différentes méthodes
@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    @PostMapping("/new")
    public UserResponseDto createUser(@RequestBody UserRequestDto reqDto) {
        User user = UserMapper.toEntity(reqDto);
        User created = userService.createUser(user);
        return UserMapper.toDto(created);
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
