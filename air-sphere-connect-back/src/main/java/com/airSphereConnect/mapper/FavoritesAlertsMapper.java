package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.*;

public class FavoritesAlertsMapper {
    // Frontend -> Backend
    public static FavoritesAlerts toEntity(FavoritesAlertsDto dto) {
        if (dto == null) return null;

        FavoritesAlerts entity = new FavoritesAlerts();
        entity.setId(dto.getId());
        entity.setEnabled(dto.getIsEnabled());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }
    // Backend -> Frontend
    public static FavoritesAlertsDto toDto(FavoritesAlerts entity) {
        if (entity == null) return null;

        FavoritesAlertsDto dto = new FavoritesAlertsDto();
        dto.setId(entity.getId());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }

        if (entity.getCity() != null) {
            dto.setCityId(entity.getCity().getId());
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
