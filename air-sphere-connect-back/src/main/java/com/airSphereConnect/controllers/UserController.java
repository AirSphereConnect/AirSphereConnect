package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//TODO Il faut ajouter l'adresse aux différentes méthodes
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Tous les utilisateurs
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    // Par nom d'utilisateur
    @GetMapping("/name")
    public UserResponseDto getUserByUsername(@RequestParam String username) {
        return UserMapper.toResponseDto(userService.getUserByUsername(username));
    }

    // Création d'un utilisateur
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto reqDto) {
        User user = UserMapper.toEntity(reqDto);
        User created = userService.createUser(user);
        UserResponseDto respDto = UserMapper.toResponseDto(created);
        return ResponseEntity.status(201).body(respDto);
    }

    // Mettre à jour un utilisateur
    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @RequestBody UserRequestDto reqDto) {
        User user = UserMapper.toEntity(reqDto);
        User updated = userService.updateUser(id, user);
        return UserMapper.toResponseDto(updated);
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Long id) {
        User deletedUser = userService.deleteUser(id);
        UserResponseDto respDto = UserMapper.toResponseDto(deletedUser);
        return ResponseEntity.ok(respDto);
    }
}
