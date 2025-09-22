package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.entities.*;

public class AlertsMapper {

    public static Alerts toEntity(AlertsDto dto) {
        if (dto == null) return null;

        Alerts entity = new Alerts();
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

        entity.setAlertType(dto.getAlertType());
        entity.setMessage(dto.getMessage());
        entity.setSentAt(dto.getSentAt());

        return entity;
    }

    public static AlertsDto toDto(Alerts entity) {
        if (entity == null) return null;

        AlertsDto dto = new AlertsDto();
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

        dto.setAlertType(entity.getAlertType());
        dto.setMessage(entity.getMessage());
        dto.setSentAt(entity.getSentAt());

        return dto;
    }

}
