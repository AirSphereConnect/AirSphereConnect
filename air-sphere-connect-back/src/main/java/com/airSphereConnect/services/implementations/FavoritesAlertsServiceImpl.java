package com.airSphereConnect.services.implementations;
import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.*;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.FavoritesAlertsMapper;
import com.airSphereConnect.repositories.FavoritesAlertsRepository;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.FavoritesAlertsService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritesAlertsServiceImpl implements FavoritesAlertsService {

    private final FavoritesAlertsRepository favoritesAlertsRepository;
    private final UserRepository userRepository;

    public FavoritesAlertsServiceImpl(FavoritesAlertsRepository favoritesAlertsRepository, UserRepository userRepository) {
        this.favoritesAlertsRepository = favoritesAlertsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<FavoritesAlertsDto> getAllFavoritesAlerts() {
        return favoritesAlertsRepository.findAll()
                .stream()
                .map(FavoritesAlertsMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<FavoritesAlertsDto> getUserAlerts(Long userId) {
        return favoritesAlertsRepository.findById(userId).stream()
                .map(FavoritesAlertsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FavoritesAlertsDto createAlertConfig(Long userId, FavoritesAlertsDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + userId));

        // Convertir null en false pour enabled
        if (dto.getEnabled() == null) {
            dto.setEnabled(false);
        }

        FavoritesAlerts entity = FavoritesAlertsMapper.toEntity(user.getId(), dto);
        entity = favoritesAlertsRepository.save(entity);
        return FavoritesAlertsMapper.toDto(entity);
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
        return FavoritesAlertsMapper.toDto(entity);
    }


    @Override
    public void deleteAlertConfig(Long alertConfigId) {
        favoritesAlertsRepository.deleteById(alertConfigId);
    }
}
