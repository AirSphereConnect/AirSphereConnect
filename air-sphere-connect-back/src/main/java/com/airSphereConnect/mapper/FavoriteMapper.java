package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.Favorite;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {
    // Backend -> Frontend
    public FavoriteDto toDto(Favorite favorite) {
        return new FavoriteDto(
                favorite.getId(),
                favorite.getFavoriteCategory(),
                favorite.getCreatedAt(),
                favorite.getUpdatedAt(),
                favorite.getUser() != null ? favorite.getUser().getId() : null,
                favorite.getCity() != null ? favorite.getCity().getId() : null
        );
    }
    // Frontend -> Backend
    public Favorite toEntity(FavoriteDto dto) {
        Favorite favorite = new Favorite();
        favorite.setId(dto.getId());
        favorite.setFavoriteCategory(dto.getFavoriteCategory());


        return favorite;
    }
}

