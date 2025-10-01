package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.entities.Alerts;
import com.airSphereConnect.mapper.AlertsMapper;
import com.airSphereConnect.repositories.AlertsRepository;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.AlertsService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertsServiceImpl implements AlertsService {
    private static final Logger logger = LoggerFactory.getLogger(AlertsServiceImpl.class);

    //MailtrapSdkEmailSender
    //EmailHogSenderImpl

    private final AlertsRepository alertsRepository;
    private final AlertsMapper alertsMapper;
    private final EmailHogSenderImpl emailSender;
    private final UserRepository userRepository;

    public AlertsServiceImpl(AlertsRepository alertsRepository, AlertsMapper alertsMapper, EmailHogSenderImpl emailSender, UserRepository userRepository) {
        this.alertsRepository = alertsRepository;
        this.alertsMapper = alertsMapper;
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }

    @Override
    public void sendAlerts(AlertsDto dto) {
        logger.debug("Envoi alerte pour userId={}, type={}, message={}", dto.getUserId(), dto.getAlertType(), dto.getMessage());
        Alerts entity = alertsMapper.toEntity(dto);

        entity.setUser(userRepository.findById(dto.getUserId()).orElseThrow());
        entity.setCity(dto.getCity());
        entity.setDepartment(dto.getDepartment());
        entity.setRegion(dto.getRegion());

        Alerts saved = alertsRepository.save(entity);
        logger.debug("Alerte sauvegardée avec id={}", saved.getId());

        userRepository.findById(dto.getUserId()).ifPresent(user -> {
            logger.debug("Envoi mail à {}", user.getEmail());
            String to = user.getEmail();
            String username = user.getUsername();
            String subject = "Nouvelle alerte : " + dto.getAlertType();
            emailSender.sendEmail(to, username, subject, dto);
        });
    }

    @Override
    public List<AlertsDto> getUserAlerts(Long userId) {
        return alertsRepository.findByUserId(userId).stream()
                .map(alertsMapper::toDto)
                .collect(Collectors.toList());
    }
}
