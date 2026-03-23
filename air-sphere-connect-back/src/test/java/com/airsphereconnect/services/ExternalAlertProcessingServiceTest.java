package com.airsphereconnect.services;

import com.airsphereconnect.dtos.ExternalAlertDto;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.FavoritesAlerts;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.entities.enums.UserRole;
import com.airsphereconnect.repositories.FavoritesAlertsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExternalAlertProcessingService Test Suite")
class ExternalAlertProcessingServiceTest {

    @Mock
    private FavoritesAlertsRepository favoritesAlertsRepository;

    @Mock
    private AlertsService alertsService;

    @InjectMocks
    private ExternalAlertProcessingService service;

    private City city;
    private User user;
    private FavoritesAlerts favoritesAlerts;
    private ExternalAlertDto alertDto;

    @BeforeEach
    void setUp() {
        city = new City();
        city.setId(1L);

        user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);

        favoritesAlerts = new FavoritesAlerts();
        favoritesAlerts.setCity(city);
        favoritesAlerts.setUser(user);

        alertDto = new ExternalAlertDto();
        alertDto.setCityId(1L);
        alertDto.setType("WEATHER");
        alertDto.setMessage("Alerte météo");
    }

    @Nested
    @DisplayName("Tests pour processExternalAlert")
    class ProcessExternalAlertTests {

        @Test
        @DisplayName("Devrait traiter une alerte avec des abonnés")
        void shouldProcessAlertWithSubscribers() {
            when(favoritesAlertsRepository.findByCityIdAndEnabled(1L, true))
                    .thenReturn(List.of(favoritesAlerts));

            service.processExternalAlert(alertDto);

            verify(alertsService, times(1)).sendAlerts(any());
        }

        @Test
        @DisplayName("Ne devrait pas envoyer d'alerte si aucun abonné")
        void shouldNotSendAlertWhenNoSubscribers() {
            when(favoritesAlertsRepository.findByCityIdAndEnabled(1L, true))
                    .thenReturn(List.of());

            service.processExternalAlert(alertDto);

            verify(alertsService, never()).sendAlerts(any());
        }

        @Test
        @DisplayName("Ne devrait pas traiter si cityId est null")
        void shouldNotProcessWhenCityIdIsNull() {
            alertDto.setCityId(null);

            service.processExternalAlert(alertDto);

            verify(favoritesAlertsRepository, never()).findByCityIdAndEnabled(any(), anyBoolean());
            verify(alertsService, never()).sendAlerts(any());
        }
    }
}
