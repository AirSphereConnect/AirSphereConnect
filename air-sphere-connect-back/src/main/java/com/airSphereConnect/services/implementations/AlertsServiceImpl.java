package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.entities.Alerts;
import com.airSphereConnect.mapper.AlertsMapper;
import com.airSphereConnect.repositories.AlertsRepository;
import com.airSphereConnect.services.AlertsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertsServiceImpl implements AlertsService {

    private final AlertsRepository alertsRepository;
    private final AlertsMapper alertsMapper;
//    private final NotificationEmailSender emailSender;

    public AlertsServiceImpl(AlertsRepository alertsRepository, AlertsMapper alertsMapper) {
        this.alertsRepository = alertsRepository;
//        this.emailSender = emailSender;
        this.alertsMapper = alertsMapper;
    }

    @Override
    public void sendAlerts(AlertsDto dto) {
        Alerts entity = AlertsMapper.toEntity(dto);
        alertsRepository.save(entity);
//        emailSender.sendEmail(entity.getUser().getEmail(), "Nouvelle alerte", entity.getMessage());
    }

    @Override
    public List<AlertsDto> getUserAlerts(Long userId) {
        return alertsRepository.findByUserId(userId).stream()
                .map(AlertsMapper::toDto)
                .collect(Collectors.toList());
    }
}

