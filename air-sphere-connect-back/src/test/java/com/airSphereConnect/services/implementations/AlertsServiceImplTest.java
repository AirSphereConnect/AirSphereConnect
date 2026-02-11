package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.entities.Alerts;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.mapper.AlertsMapper;
import com.airSphereConnect.repositories.AlertsRepository;
import com.airSphereConnect.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertsServiceImpl Test Suite")
class AlertsServiceImplTest {

    @Mock
    private AlertsRepository alertsRepository;
    @Mock
    private AlertsMapper alertsMapper;
    @Mock
    private EmailHogSenderImpl emailSender;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlertsServiceImpl alertsService;

    private User user;
    private Alerts alert;
    private AlertsDto alertsDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        alert = new Alerts();
        alert.setId(1L);
        alert.setUser(user);
        alert.setMessage("Test alert");

        alertsDto = new AlertsDto();
        alertsDto.setUserId(1L);
        alertsDto.setMessage("Test alert");
    }

    @Test
    @DisplayName("should send alert and save to database")
    void sendAlerts_shouldSaveAndSendEmail() {
        when(alertsMapper.toEntity(alertsDto)).thenReturn(alert);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(alertsRepository.save(any(Alerts.class))).thenReturn(alert);

        alertsService.sendAlerts(alertsDto);

        verify(alertsRepository).save(any(Alerts.class));
        verify(emailSender).sendEmail(eq("test@example.com"), eq("testuser"), anyString(), eq(alertsDto));
    }

    @Test
    @DisplayName("should throw when user not found during send")
    void sendAlerts_shouldThrowWhenUserNotFound() {
        when(alertsMapper.toEntity(alertsDto)).thenReturn(alert);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> alertsService.sendAlerts(alertsDto));
    }

    @Test
    @DisplayName("should return user alerts as DTOs")
    void getUserAlerts_shouldReturnDtos() {
        when(alertsRepository.findByUserId(1L)).thenReturn(List.of(alert));
        when(alertsMapper.toDto(alert)).thenReturn(alertsDto);

        List<AlertsDto> result = alertsService.getUserAlerts(1L);

        assertEquals(1, result.size());
        verify(alertsRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("should return empty list when no alerts")
    void getUserAlerts_shouldReturnEmpty() {
        when(alertsRepository.findByUserId(99L)).thenReturn(List.of());

        List<AlertsDto> result = alertsService.getUserAlerts(99L);

        assertTrue(result.isEmpty());
    }
}
