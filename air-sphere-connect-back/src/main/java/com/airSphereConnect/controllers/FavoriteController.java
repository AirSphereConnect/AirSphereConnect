package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.FavoriteService;
import com.airSphereConnect.services.implementations.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    public FavoriteController(FavoriteService favoriteService, UserRepository userRepository) {
        this.favoriteService = favoriteService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<FavoriteDto> getAllFavorites() {
        return favoriteService.getAllFavorites();
    }

    @GetMapping("/{id}")
    public FavoriteDto getFavoriteById(@PathVariable Long id) {
        return favoriteService.getFavoriteById(id);
    }

    @PostMapping("/new")
    public ResponseEntity<FavoriteDto> createFavorite(
            @RequestBody FavoriteDto favoriteDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé"));

        FavoriteDto created = favoriteService.createFavorite(user.getId(), favoriteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public FavoriteDto updateFavorite(@PathVariable Long id, @RequestBody FavoriteDto favoriteDto) {
        return favoriteService.updateFavorite(id, favoriteDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FavoriteDto> deleteFavorite(@PathVariable Long id) {
        System.out.println("id entrée delete : " + id);
        FavoriteDto deletedFavorite = favoriteService.deleteFavorite(id);
        System.out.println("delete : " + deletedFavorite);
        return ResponseEntity.ok(deletedFavorite);
    }
}

