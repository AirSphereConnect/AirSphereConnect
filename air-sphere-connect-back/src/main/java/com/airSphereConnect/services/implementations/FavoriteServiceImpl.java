package com.airSphereConnect.services.implementations;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Favorite;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.FavoriteMapper;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.FavoriteRepository;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final UserRepository userRepository;

    private final CityRepository cityRepository;

    private final FavoriteMapper favoriteMapper;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository, CityRepository cityRepository, FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.favoriteMapper = favoriteMapper;
    }

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
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Favori non trouvé avec l'id : " + id));
        return favoriteMapper.toDto(favorite);
    }

    @Override
    public FavoriteDto createFavorite(Long userId, FavoriteDto favoriteDto) {
        if (userRepository.findById(userId).isEmpty()) {
            userRepository.findById(userId).orElseThrow(() ->
                    new GlobalException.RessourceNotFoundException("Utilisateur non trouvé avec l'id : " + userId));
        }

        favoriteDto.setUserId(userId);
        Favorite favorite = favoriteMapper.toEntity(favoriteDto);
        favorite.setCreatedAt(LocalDateTime.now());
        Favorite saved = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(saved);
    }

    @Override
    public FavoriteDto updateFavorite(Long id, FavoriteDto favoriteDto) {

        Favorite existing = favoriteRepository.findById(id)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Favori non trouvé avec l'id : " + id));

        if (favoriteDto.getCityId() != null) {
            City city = cityRepository.findById(favoriteDto.getCityId())
                    .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Ville non trouvée"));
            existing.setCity(city);
        }

        existing.setFavoriteCategory(favoriteDto.getFavoriteCategory());
        existing.setUpdatedAt(LocalDateTime.now());

        Favorite updated = favoriteRepository.save(existing);
        return favoriteMapper.toDto(updated);
    }

    @Override
    public FavoriteDto deleteFavorite(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Favori non trouvé avec l'id : " + id));

        favorite.setDeleteAt(LocalDateTime.now());
        Favorite saved = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(saved);
    }
}
