package com.airsphereconnect.scheduler;

import com.airsphereconnect.services.api.DataSyncService;
import com.airsphereconnect.services.api.HistoricalDataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiDataSyncScheduler Test Suite")
class ApiDataSyncSchedulerTest {

    @Mock
    private SyncMetrics syncMetrics;

    @Mock
    private HistoricalDataLoaderService historicalDataLoader;

    @Mock
    private DataSyncService weatherService;

    @Mock
    private DataSyncService airQualityService;

    private ApiDataSyncScheduler scheduler;

    @BeforeEach
    void setUp() {
        lenient().when(weatherService.getServiceName()).thenReturn("WEATHER");
        lenient().when(weatherService.isEnabled()).thenReturn(true);
        lenient().when(weatherService.getLastSync()).thenReturn(null);

        lenient().when(airQualityService.getServiceName()).thenReturn("AIR_QUALITY");
        lenient().when(airQualityService.isEnabled()).thenReturn(true);
        lenient().when(airQualityService.getLastSync()).thenReturn(null);

        scheduler = new ApiDataSyncScheduler(
                List.of(weatherService, airQualityService),
                syncMetrics,
                historicalDataLoader
        );
    }

    @Nested
    @DisplayName("Tests pour forceSyncService")
    class ForceSyncServiceTests {

        @Test
        @DisplayName("Devrait forcer la sync d'un service existant")
        void shouldForceSyncExistingService() {
            assertDoesNotThrow(() -> scheduler.forceSyncService("WEATHER"));
            verify(syncMetrics).recordServiceSync(eq("WEATHER"), anyLong(), eq(true));
        }

        @Test
        @DisplayName("Devrait échouer si le service n'existe pas")
        void shouldThrowWhenServiceNotFound() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> scheduler.forceSyncService("UNKNOWN_SERVICE")
            );
        }
    }

    @Nested
    @DisplayName("Tests pour forceSyncAll")
    class ForceSyncAllTests {

        @Test
        @DisplayName("Devrait synchroniser tous les services activés")
        void shouldSyncAllEnabledServices() {
            assertDoesNotThrow(() -> scheduler.forceSyncAll());
            verify(syncMetrics, times(2)).recordServiceSync(anyString(), anyLong(), eq(true));
        }
    }

    @Nested
    @DisplayName("Tests pour nightlyMissingDataCheck")
    class NightlyMissingDataCheckTests {

        @Test
        @DisplayName("Devrait vérifier les jours manquants sans erreur")
        void shouldCheckMissingDataSuccessfully() {
            when(historicalDataLoader.detectAndFillMissingDays()).thenReturn(0);

            assertDoesNotThrow(() -> scheduler.nightlyMissingDataCheck());
            verify(historicalDataLoader).detectAndFillMissingDays();
        }

        @Test
        @DisplayName("Devrait gérer les erreurs sans propager l'exception")
        void shouldHandleErrorsGracefully() {
            when(historicalDataLoader.detectAndFillMissingDays()).thenThrow(new RuntimeException("DB error"));

            assertDoesNotThrow(() -> scheduler.nightlyMissingDataCheck());
        }
    }
}
