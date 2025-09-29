package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.entities.*;
import org.springframework.stereotype.Component;

@Component
public class AlertsMapper {

        public static AlertsDto toDto(Alerts alerts) {
            if (alerts == null) return null;
            AlertsDto dto = new AlertsDto();
            copyAlertsToDto(alerts, dto);
            return dto;
        }

        public static Alerts toEntity(AlertsDto dto) {
            if (dto == null) return null;
            Alerts alerts = new Alerts();
            copyDtoToAlerts(dto, alerts);
            return alerts;
        }

        private static void copyAlertsToDto(Alerts alerts, AlertsDto dto) {
            dto.setId(alerts.getId());
            dto.setAlertType(alerts.getAlertType());
            dto.setMessage(alerts.getMessage());
            dto.setSentAt(alerts.getSentAt());
            dto.setCity(alerts.getCity());
            dto.setRegion(alerts.getRegion());
            dto.setDepartmentId(alerts.getDepartment());
            if (alerts.getUser() != null) {
                dto.setUserId(alerts.getUser().getId());
            }
        }

        private static void copyDtoToAlerts(AlertsDto dto, Alerts alerts) {
            alerts.setId(dto.getId());
            alerts.setAlertType(dto.getAlertType());
            alerts.setMessage(dto.getMessage());
            alerts.setSentAt(dto.getSentAt());
            alerts.setCity(dto.getCity());
            alerts.setRegion(dto.getRegion());
            alerts.setDepartment(dto.getDepartment());
            // author à set dans service
        }
    }
