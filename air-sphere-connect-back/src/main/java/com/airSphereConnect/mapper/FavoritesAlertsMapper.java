package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.*;
import org.springframework.stereotype.Component;

@Component
public class FavoritesAlertsMapper {

    // Frontend → Backend
    public static FavoritesAlerts toEntity(FavoritesAlertsDto dto) {
        if (dto == null) return null;

        FavoritesAlerts entity = new FavoritesAlerts();
        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            entity.setUser(user);
        } else {
            entity.setUser(null);
        }
        entity.setId(dto.getId());
        entity.setEnabled(dto.getIsEnabled());

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


        return entity;
    }

    // Backend → Frontend
    public static FavoritesAlertsDto toDto(FavoritesAlerts entity) {
        if (entity == null) return null;

        FavoritesAlertsDto dto = new FavoritesAlertsDto();
        dto.setId(entity.getId());

        if (entity.getUser() != null) {
            dto.setUser(entity.getUser().getId());
        }

        if (entity.getCity() != null) {
            dto.setCityName(entity.getCity().getName());
        }

        if (entity.getDepartment() != null) {
            dto.setDepartmentId(entity.getDepartment().getId());
        }

        if (entity.getRegion() != null) {
            dto.setRegionId(entity.getRegion().getId());
        }

        dto.setEnabled(entity.getIsEnabled());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}
