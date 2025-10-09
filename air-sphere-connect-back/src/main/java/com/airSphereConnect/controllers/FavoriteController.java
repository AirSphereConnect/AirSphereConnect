package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<FavoriteDto> getAllFavorites() {
        return favoriteService.getAllFavorites();
    }

    @GetMapping("/{id}")
    public FavoriteDto getFavoriteById(@PathVariable Long id) {
        return favoriteService.getFavoriteById(id);
    }

    @PostMapping("/new/{userId}")
    public ResponseEntity<FavoriteDto> createFavorite(@PathVariable Long userId, @RequestBody FavoriteDto favoriteDto) {
        FavoriteDto created = favoriteService.createFavorite(userId, favoriteDto);
        return ResponseEntity.status(201).body(created);
    }


    @PutMapping("/{id}")
    public FavoriteDto updateFavorite(@PathVariable Long id, @RequestBody FavoriteDto favoriteDto) {
        return favoriteService.updateFavorite(id, favoriteDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FavoriteDto> deleteFavorite(@PathVariable Long id) {
        FavoriteDto deletedFavorite = favoriteService.deleteFavorite(id);
        return ResponseEntity.ok(deletedFavorite);
    }
}

