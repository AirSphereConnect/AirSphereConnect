package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.FavoriteDto;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.Favorite;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.FavoriteMapper;
import com.airsphereconnect.repositories.CityRepository;
import com.airsphereconnect.repositories.FavoriteRepository;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final String FAVORITE_NOT_FOUND = "Favori non trouvé avec l'id : ";

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
        return favoriteRepository.findByDeletedAtIsNull()
                .stream()
                .map(favoriteMapper::toDto)
                .toList();
    }


    @Override
    public FavoriteDto getFavoriteById(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException(FAVORITE_NOT_FOUND + id));
        return favoriteMapper.toDto(favorite);
    }

    @Override
    public FavoriteDto createFavorite(Long userId, FavoriteDto favoriteDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + userId));

        City city = null;
        if (favoriteDto.getCityId() != null) {
            city = cityRepository.findById(favoriteDto.getCityId())
                    .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Ville non trouvée avec l'id : " + favoriteDto.getCityId()));
        }

        if (favoriteDto.getSelectAirQuality() == null) {
            favoriteDto.setSelectAirQuality(false);
        }
        if (favoriteDto.getSelectWeather() == null) {
            favoriteDto.setSelectWeather(false);
        }
        if (favoriteDto.getSelectPopulation() == null) {
            favoriteDto.setSelectPopulation(false);
        }

        Favorite favorite = favoriteMapper.toEntity(favoriteDto);
        favorite.setUser(user);
        favorite.setCity(city);

        Favorite saved = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(saved);
    }


    @Override
    public FavoriteDto updateFavorite(Long id, FavoriteDto favoriteDto) {
        //Voir s'il faut contrôler si current user est autorisé à modifier son favori
        Favorite existing = favoriteRepository.findById(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException(FAVORITE_NOT_FOUND + id));

        if (favoriteDto.getCityId() != null) {
            City city = cityRepository.findById(favoriteDto.getCityId())
                    .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Ville non trouvée"));
            existing.setCity(city);
        }

        existing.setSelectAirQuality(favoriteDto.getSelectAirQuality());
        existing.setSelectPopulation(favoriteDto.getSelectPopulation());
        existing.setSelectWeather(favoriteDto.getSelectWeather());

        Favorite updated = favoriteRepository.save(existing);
        return favoriteMapper.toDto(updated);
    }

    @Override
    public FavoriteDto deleteFavorite(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException(FAVORITE_NOT_FOUND + id));

        favorite.softDelete();
        Favorite saved = favoriteRepository.save(favorite);
        return favoriteMapper.toDto(saved);
    }
}
