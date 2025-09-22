package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.*;

public class FavoritesAlertsMapper {

    public static FavoritesAlerts toEntity(FavoritesAlertsDto dto) {
        if (dto == null) return null;

        FavoritesAlerts entity = new FavoritesAlerts();

        entity.setId(dto.getId());

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
        }

        if (dto.getRegionId() != null) {
            Region region = new Region();
            region.setId(dto.getRegionId());
            entity.setRegion(region);
        }

        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

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

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

}
