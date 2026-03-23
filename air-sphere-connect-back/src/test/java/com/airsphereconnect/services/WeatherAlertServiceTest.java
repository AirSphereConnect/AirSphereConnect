package com.airsphereconnect.services;

import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.WeatherMeasurement;
import com.airsphereconnect.repositories.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WeatherAlertService Test Suite")
class WeatherAlertServiceTest {

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private ExternalAlertProcessingService externalAlertProcessingService;

    @InjectMocks
    private WeatherAlertService weatherAlertService;

    private WeatherMeasurement weatherWithAlert;
    private City city;

    @BeforeEach
    void setUp() {
        city = new City();
        city.setId(1L);

        weatherWithAlert = new WeatherMeasurement();
        weatherWithAlert.setCity(city);
        weatherWithAlert.setAlertMessage("Alerte météo : vent violent");
    }

    @Nested
    @DisplayName("Tests pour les méthodes DataSyncService")
    class DataSyncServiceTests {

        @Test
        @DisplayName("Devrait retourner le nom du service")
        void shouldReturnServiceName() {
            assertEquals("WeatherSyncService", weatherAlertService.getServiceName());
        }

        @Test
        @DisplayName("Devrait être activé")
        void shouldBeEnabled() {
            assertTrue(weatherAlertService.isEnabled());
        }

        @Test
        @DisplayName("Devrait retourner l'intervalle de synchronisation (24h)")
        void shouldReturnSyncInterval() {
            assertEquals(Duration.ofHours(24), weatherAlertService.getSyncInterval());
        }

        @Test
        @DisplayName("Devrait retourner null comme dernière sync initiale")
        void shouldReturnNullForInitialLastSync() {
            assertNull(weatherAlertService.getLastSync());
        }

        @Test
        @DisplayName("Devrait retourner 0 pour les erreurs consécutives initiales")
        void shouldReturnZeroForInitialConsecutiveErrors() {
            assertEquals(0, weatherAlertService.getConsecutiveErrors());
        }
    }

    @Nested
    @DisplayName("Tests pour syncData")
    class SyncDataTests {

        @Test
        @DisplayName("Devrait synchroniser avec succès")
        void shouldSyncDataSuccessfully() {
            when(weatherRepository.findByMeasuredAtBetweenAndAlertTrue(any(), any()))
                    .thenReturn(List.of(weatherWithAlert));

            weatherAlertService.syncData();

            verify(externalAlertProcessingService).processExternalAlert(any());
            assertNotNull(weatherAlertService.getLastSync());
            assertEquals(0, weatherAlertService.getConsecutiveErrors());
        }

        @Test
        @DisplayName("Devrait incrémenter les erreurs en cas d'exception")
        void shouldIncrementErrorsOnException() {
            when(weatherRepository.findByMeasuredAtBetweenAndAlertTrue(any(), any()))
                    .thenThrow(new RuntimeException("Erreur DB"));

            weatherAlertService.syncData();

            assertEquals(1, weatherAlertService.getConsecutiveErrors());
        }
    }

    @Nested
    @DisplayName("Tests pour checkAndProcessAlertsForToday")
    class CheckAlertsTests {

        @Test
        @DisplayName("Devrait traiter les alertes du jour")
        void shouldProcessTodayAlerts() {
            when(weatherRepository.findByMeasuredAtBetweenAndAlertTrue(any(), any()))
                    .thenReturn(List.of(weatherWithAlert));

            weatherAlertService.checkAndProcessAlertsForToday();

            verify(externalAlertProcessingService, times(1)).processExternalAlert(any());
        }

        @Test
        @DisplayName("Ne devrait pas appeler processExternalAlert si aucune alerte")
        void shouldNotProcessWhenNoAlerts() {
            when(weatherRepository.findByMeasuredAtBetweenAndAlertTrue(any(), any()))
                    .thenReturn(List.of());

            weatherAlertService.checkAndProcessAlertsForToday();

            verify(externalAlertProcessingService, never()).processExternalAlert(any());
        }
    }
}
