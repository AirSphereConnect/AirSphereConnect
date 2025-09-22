package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.FavoritesAlerts;
import com.airSphereConnect.mapper.FavoritesAlertsMapper;
import com.airSphereConnect.repositories.FavoritesAlertsRepository;
import com.airSphereConnect.services.FavoritesAlertsService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritesAlertsServiceImpl implements FavoritesAlertsService {

    private final FavoritesAlertsRepository favoritesAlertsRepository;

    public FavoritesAlertsServiceImpl(FavoritesAlertsRepository favoritesAlertsRepository) {
        this.favoritesAlertsRepository= favoritesAlertsRepository;
    }

    @Override
    public FavoritesAlertsDto createAlertConfig(FavoritesAlertsDto dto) {
        FavoritesAlerts entity = FavoritesAlertsMapper.toEntity(dto);
        entity = favoritesAlertsRepository.save(entity);
        return FavoritesAlertsMapper.toDto(entity);
    }

    @Override
    public List<FavoritesAlertsDto> getUserAlerts(Long userId) {
        return favoritesAlertsRepository.findByUserId(userId).stream()
                .map(FavoritesAlertsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FavoritesAlertsDto updateAlertConfig(FavoritesAlertsDto dto) {
        FavoritesAlerts entity = favoritesAlertsRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Alert config not found"));
        // Mettre à jour les relations si nécessaire
        entity = favoritesAlertsRepository.save(entity);
        return FavoritesAlertsMapper.toDto(entity);
    }

    @Override
    public void setAlertEnabled(Long alertConfigId, boolean enabled) {
        FavoritesAlerts entity = favoritesAlertsRepository.findById(alertConfigId)
                .orElseThrow(() -> new IllegalArgumentException("Alert config not found"));
        favoritesAlertsRepository.save(entity);
    }

    @Override
    public void deleteAlertConfig(Long alertConfigId) {
        favoritesAlertsRepository.deleteById(alertConfigId);
    }
}

