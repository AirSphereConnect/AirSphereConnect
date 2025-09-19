package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Favorite;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    private final UserRepository userRepository;

    private final CityRepository cityRepository;

    public FavoriteMapper(UserRepository userRepository, CityRepository cityRepository) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
    }

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

    public Favorite toEntity(FavoriteDto dto) {
        Favorite favorite = new Favorite();
        favorite.setId(dto.getId());
        favorite.setFavoriteCategory(dto.getFavoriteCategory());
        favorite.setCreatedAt(dto.getCreatedAt());
        favorite.setUpdatedAt(dto.getUpdatedAt());

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Utilisateur non trouvé"));
            favorite.setUser(user);
        }

        if (dto.getCityId() != null) {
            City city = cityRepository.findById(dto.getCityId())
                    .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Ville non trouvée"));
            favorite.setCity(city);
        }
        return favorite;
    }
}
