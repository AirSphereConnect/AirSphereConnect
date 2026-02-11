package com.airSphereConnect.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SyncMetrics Test Suite")
class SyncMetricsTest {

    private SyncMetrics syncMetrics;

    @BeforeEach
    void setUp() {
        syncMetrics = new SyncMetrics();
    }

    @Nested
    @DisplayName("recordGlobalSync tests")
    class RecordGlobalSyncTests {

        @Test
        @DisplayName("should record global sync event")
        void recordGlobalSync_shouldRecordEvent() {
            AtomicInteger successCount = new AtomicInteger(5);
            AtomicInteger errorCount = new AtomicInteger(1);

            syncMetrics.recordGlobalSync(successCount, errorCount);

            List<SyncMetrics.SyncEvent> history = syncMetrics.getRecentHistory();
            assertThat(history).hasSize(1);
            assertThat(history.get(0).getServiceName()).isEqualTo("GLOBAL");
            assertThat(history.get(0).getDurationMs()).isEqualTo(6);
        }
    }

    @Nested
    @DisplayName("recordServiceSync tests")
    class RecordServiceSyncTests {

        @Test
        @DisplayName("should record service sync with success")
        void recordServiceSync_success_shouldRecordEvent() {
            syncMetrics.recordServiceSync("weather", 1500, true);

            SyncMetrics.ServiceStats stats = syncMetrics.getServiceStats("weather");
            assertThat(stats.getTotalSyncs()).isEqualTo(1);
            assertThat(stats.getSuccessCount()).isEqualTo(1);
            assertThat(stats.getErrorCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("should record service sync with error")
        void recordServiceSync_error_shouldRecordEvent() {
            syncMetrics.recordServiceSync("air-quality", 500, false);

            SyncMetrics.ServiceStats stats = syncMetrics.getServiceStats("air-quality");
            assertThat(stats.getTotalSyncs()).isEqualTo(1);
            assertThat(stats.getSuccessCount()).isEqualTo(0);
            assertThat(stats.getErrorCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("should accumulate stats for same service")
        void recordServiceSync_shouldAccumulateStats() {
            syncMetrics.recordServiceSync("weather", 1000, true);
            syncMetrics.recordServiceSync("weather", 2000, true);
            syncMetrics.recordServiceSync("weather", 1500, false);

            SyncMetrics.ServiceStats stats = syncMetrics.getServiceStats("weather");
            assertThat(stats.getTotalSyncs()).isEqualTo(3);
            assertThat(stats.getSuccessCount()).isEqualTo(2);
            assertThat(stats.getErrorCount()).isEqualTo(1);
            assertThat(stats.getAverageDurationMs()).isEqualTo(1500);
        }
    }

    @Nested
    @DisplayName("getRecentHistory tests")
    class GetRecentHistoryTests {

        @Test
        @DisplayName("should return empty list when no events")
        void getRecentHistory_shouldReturnEmptyList() {
            List<SyncMetrics.SyncEvent> history = syncMetrics.getRecentHistory();
            assertThat(history).isEmpty();
        }

        @Test
        @DisplayName("should return last 20 events")
        void getRecentHistory_shouldReturnLast20Events() {
            for (int i = 0; i < 25; i++) {
                syncMetrics.recordServiceSync("service" + i, 100, true);
            }

            List<SyncMetrics.SyncEvent> history = syncMetrics.getRecentHistory();
            assertThat(history).hasSize(20);
            assertThat(history.get(0).getServiceName()).isEqualTo("service5");
        }
    }

    @Nested
    @DisplayName("getServiceStats tests")
    class GetServiceStatsTests {

        @Test
        @DisplayName("should return empty stats for unknown service")
        void getServiceStats_unknownService_shouldReturnEmptyStats() {
            SyncMetrics.ServiceStats stats = syncMetrics.getServiceStats("unknown");

            assertThat(stats.getTotalSyncs()).isEqualTo(0);
            assertThat(stats.getSuccessCount()).isEqualTo(0);
            assertThat(stats.getErrorCount()).isEqualTo(0);
            assertThat(stats.getAverageDurationMs()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("getAllStats tests")
    class GetAllStatsTests {

        @Test
        @DisplayName("should return all service stats")
        void getAllStats_shouldReturnAllStats() {
            syncMetrics.recordServiceSync("weather", 1000, true);
            syncMetrics.recordServiceSync("air-quality", 500, true);

            Map<String, SyncMetrics.ServiceStats> allStats = syncMetrics.getAllStats();

            assertThat(allStats).hasSize(2);
            assertThat(allStats).containsKeys("weather", "air-quality");
        }
    }

    @Nested
    @DisplayName("reset tests")
    class ResetTests {

        @Test
        @DisplayName("should clear all data")
        void reset_shouldClearAllData() {
            syncMetrics.recordServiceSync("weather", 1000, true);
            syncMetrics.recordServiceSync("air-quality", 500, true);

            syncMetrics.reset();

            assertThat(syncMetrics.getRecentHistory()).isEmpty();
            assertThat(syncMetrics.getAllStats()).isEmpty();
        }
    }

    @Nested
    @DisplayName("ServiceStats tests")
    class ServiceStatsTests {

        @Test
        @DisplayName("getSuccessRate should return correct percentage")
        void getSuccessRate_shouldReturnCorrectPercentage() {
            syncMetrics.recordServiceSync("weather", 1000, true);
            syncMetrics.recordServiceSync("weather", 1000, true);
            syncMetrics.recordServiceSync("weather", 1000, false);
            syncMetrics.recordServiceSync("weather", 1000, false);

            SyncMetrics.ServiceStats stats = syncMetrics.getServiceStats("weather");
            assertThat(stats.getSuccessRate()).isEqualTo(50.0);
        }

        @Test
        @DisplayName("getSuccessRate should return 0 when no syncs")
        void getSuccessRate_noSyncs_shouldReturnZero() {
            SyncMetrics.ServiceStats stats = syncMetrics.getServiceStats("unknown");
            assertThat(stats.getSuccessRate()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("getLastSync should return last sync time")
        void getLastSync_shouldReturnLastSyncTime() {
            syncMetrics.recordServiceSync("weather", 1000, true);

            SyncMetrics.ServiceStats stats = syncMetrics.getServiceStats("weather");
            assertThat(stats.getLastSync()).isNotNull();
        }
    }
}
