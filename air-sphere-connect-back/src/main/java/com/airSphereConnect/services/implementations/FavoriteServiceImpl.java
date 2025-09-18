package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Favorite;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.FavoriteMapper;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.FavoriteRepository;
import com.airSphereConnect.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public List<FavoriteDto> getAllFavorites() {
        return favoriteRepository.findByDeleteAtIsNull()
                .stream()
                .map(favoriteMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public FavoriteDto getFavoriteById(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new GlobalException.FavoriteNotFoundException("Favori non trouvé avec l'id : " + id));
        return favoriteMapper.toDto(favorite);
    }

    @Override
    public FavoriteDto createFavorite(Long userId, FavoriteDto favoriteDto) {
        favoriteDto.setUserId(userId);
        Favorite favorite = favoriteMapper.toEntity(favoriteDto);
        favorite.setCreatedAt(LocalDateTime.now());
        Favorite saved = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(saved);
    }

    @Override
    public FavoriteDto updateFavorite(Long id, FavoriteDto favoriteDto) {
        Favorite existing = favoriteRepository.findById(id)
                .orElseThrow(() -> new GlobalException.FavoriteNotFoundException("Favori non trouvé avec l'id : " + id));

        existing.setFavoriteCategory(favoriteDto.getFavoriteCategory());
        if (favoriteDto.getCityId() != null) {
            City city = cityRepository.findById(favoriteDto.getCityId())
                    .orElseThrow(() -> new GlobalException.CityNotFoundException("Ville non trouvée"));
            existing.setCity(city);
        }
        existing.setUpdatedAt(LocalDateTime.now());

        Favorite updated = favoriteRepository.save(existing);
        return favoriteMapper.toDto(updated);
    }

    @Override
    public FavoriteDto deleteFavorite(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new GlobalException.FavoriteNotFoundException("Favori non trouvé avec l'id : " + id));
        favorite.setDeleteAt(LocalDateTime.now());
        Favorite saved = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(saved);
    }
}
