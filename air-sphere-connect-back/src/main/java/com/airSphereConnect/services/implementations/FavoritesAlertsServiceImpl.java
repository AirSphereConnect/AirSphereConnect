package com.airSphereConnect.services.implementations;
import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.*;
import com.airSphereConnect.mapper.FavoritesAlertsMapper;
import com.airSphereConnect.repositories.FavoritesAlertsRepository;
import com.airSphereConnect.services.FavoritesAlertsService;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritesAlertsServiceImpl implements FavoritesAlertsService {

    private final FavoritesAlertsRepository favoritesAlertsRepository;

    public FavoritesAlertsServiceImpl(FavoritesAlertsRepository favoritesAlertsRepository) {
        this.favoritesAlertsRepository = favoritesAlertsRepository;
    }

    @Override
    public List<FavoritesAlertsDto> getAllFavoritesAlerts() {
        return favoritesAlertsRepository.findAll()
                .stream()
                .map(FavoritesAlertsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FavoritesAlertsDto createAlertConfig(FavoritesAlertsDto dto) {
        FavoritesAlerts entity = FavoritesAlertsMapper.toEntity(dto);
        entity = favoritesAlertsRepository.save(entity);
        return FavoritesAlertsMapper.toDto(entity);
    }

    @Override
    public List<FavoritesAlertsDto> getUserAlerts(Long userId) {
        return favoritesAlertsRepository.findById(userId).stream()
                .map(FavoritesAlertsMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FavoritesAlertsDto updateAlertConfig(FavoritesAlertsDto dto, Long userId) {
        FavoritesAlerts entity = favoritesAlertsRepository.findByIdAndUserId(dto.getId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Alert config not found or not owned by this user"));

        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            entity.setUser(user);
        }

        if (dto.getCityId() != null) {
            City city = new City();
            city.setId(dto.getCityId());
            entity.setCity(city);
        }

        if (dto.getDepartmentId() != null) {
            Department department = new Department();
            department.setId(dto.getDepartmentId());
            entity.setDepartment(department);
        } else {
            entity.setDepartment(null);
        }

        if (dto.getRegionId() != null) {
            Region region = new Region();
            region.setId(dto.getRegionId());
            entity.setRegion(region);
        }

        entity.setEnabled(dto.getIsEnabled());

        entity = favoritesAlertsRepository.save(entity);
        return FavoritesAlertsMapper.toDto(entity);
    }

    @Override
    public void setAlertEnabled(Long alertConfigId, boolean enabled) {
        FavoritesAlerts entity = favoritesAlertsRepository.findById(alertConfigId)
                .orElseThrow(() -> new IllegalArgumentException("Alert config not found"));
        entity.setEnabled(enabled);
        favoritesAlertsRepository.save(entity);
    }

    @Override
    public void deleteAlertConfig(Long alertConfigId) {
        favoritesAlertsRepository.deleteById(alertConfigId);
    }
}
