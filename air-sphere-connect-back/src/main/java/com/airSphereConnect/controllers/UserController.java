package com.airSphereConnect.controllers;

import com.airSphereConnect.entities.User;
import com.airSphereConnect.services.implementations.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("?name={username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
    }

    @PostMapping
    //Ajouter @Valid lorsque spring sécurity est activé
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    //Ajouter @Valid lorsque spring sécurity est activé
    public User updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

