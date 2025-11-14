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
                favorite.getSelectAirQuality(),
                favorite.getSelectPopulation(),
                favorite.getSelectWeather(),
                favorite.getCreatedAt(),
                favorite.getUpdatedAt(),
                favorite.getUser() != null ? favorite.getUser().getId() : null,
                favorite.getCity() != null ? favorite.getCity().getId() : null,
                favorite.getCity() != null ? favorite.getCity().getName() : null
        );
    }
    // Frontend -> Backend
    public Favorite toEntity(FavoriteDto dto) {
        Favorite favorite = new Favorite();
        favorite.setId(dto.getId());
        favorite.setSelectAirQuality(dto.getSelectAirQuality());
        favorite.setSelectPopulation(dto.getSelectPopulation());
        favorite.setSelectWeather(dto.getSelectWeather());

        return favorite;
    }
}

