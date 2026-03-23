package com.airsphereconnect.services.implementations;
import com.airsphereconnect.dtos.FavoritesAlertsDto;
import com.airsphereconnect.entities.*;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.FavoritesAlertsMapper;
import com.airsphereconnect.repositories.FavoritesAlertsRepository;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.FavoritesAlertsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritesAlertsServiceImpl implements FavoritesAlertsService {
    private final FavoritesAlertsMapper  favoritesAlertsMapper;
    private final FavoritesAlertsRepository favoritesAlertsRepository;
    private final UserRepository userRepository;

    public FavoritesAlertsServiceImpl(FavoritesAlertsMapper favoritesAlertsMapper, FavoritesAlertsRepository favoritesAlertsRepository, UserRepository userRepository) {
        this.favoritesAlertsMapper = favoritesAlertsMapper;
        this.favoritesAlertsRepository = favoritesAlertsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<FavoritesAlertsDto> getAllFavoritesAlerts() {
        return favoritesAlertsRepository.findAll()
                .stream()
                .map(favoritesAlertsMapper::toDto)
                .toList();
    }


    @Override
    public List<FavoritesAlertsDto> getUserAlerts(Long userId) {
        return favoritesAlertsRepository.findById(userId).stream()
                .map(favoritesAlertsMapper::toDto)
                .toList();
    }

    @Override
    public FavoritesAlertsDto createAlertConfig(Long userId, FavoritesAlertsDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + userId));

        if (dto.getEnabled() == null) {
            dto.setEnabled(false);
        }

        FavoritesAlerts entity = FavoritesAlertsMapper.toEntity(user.getId(), dto);
        entity = favoritesAlertsRepository.save(entity);
        return favoritesAlertsMapper.toDto(entity);
    }

    @Override
    public FavoritesAlertsDto updateAlertConfig(FavoritesAlertsDto dto, Long userId, Long id) {
        FavoritesAlerts entity = favoritesAlertsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Alert config not found or not owned by this user"));

        // User
        if (dto.getUser() != null) {
            User user = new User();
            user.setId(dto.getUser());
            entity.setUser(user);
        }

        // City
        if (dto.getCityId() != null) {
            City city = new City();
            city.setId(dto.getCityId());
            entity.setCity(city);
        }

        // Enabled : convert null → false
        entity.setEnabled(Boolean.TRUE.equals(dto.getEnabled()));

        entity = favoritesAlertsRepository.save(entity);
        return favoritesAlertsMapper.toDto(entity);
    }


    @Override
    public void deleteAlertConfig(Long alertConfigId) {
        favoritesAlertsRepository.deleteById(alertConfigId);
    }
}
