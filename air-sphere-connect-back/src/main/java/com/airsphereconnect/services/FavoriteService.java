package com.airsphereconnect.services;

import com.airsphereconnect.dtos.FavoriteDto;
import java.util.List;

public interface FavoriteService {
    List<FavoriteDto> getAllFavorites();

    FavoriteDto getFavoriteById(Long favoriteId);

    FavoriteDto createFavorite(Long userId, FavoriteDto favoriteDto);

    FavoriteDto updateFavorite(Long id, FavoriteDto favoriteDto);

    FavoriteDto deleteFavorite(Long id);


}
