package com.airSphereConnect.scheduler;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SyncMetrics {

    private final Deque<SyncEvent> syncHistory = new LinkedList<>();
    private final int MAX_HISTORY = 100;

    private final Map<String, ServiceStats> serviceStats = new ConcurrentHashMap<>();

    public void recordGlobalSync(AtomicInteger successCount, AtomicInteger errorCount) {
        SyncEvent event = new SyncEvent(
                "GLOBAL",
                LocalDateTime.now(),
                successCount.get() + errorCount.get(),
                successCount.get() > 0
        );

        addToHistory(event);
    }

    public void recordServiceSync(String serviceName, long durationMs, boolean success) {
        SyncEvent event = new SyncEvent(
                serviceName,
                LocalDateTime.now(),
                durationMs,
                success
        );
        addToHistory(event);

        serviceStats.computeIfAbsent(serviceName, key -> new ServiceStats())
                .recordSync(durationMs, success);
    }

    private synchronized void addToHistory(SyncEvent event) {
        if (syncHistory.size() >= MAX_HISTORY) {
            syncHistory.removeFirst();
        }
        syncHistory.addLast(event);
    }

    public List<SyncEvent> getRecentHistory() {
        synchronized (syncHistory) {
            return syncHistory.stream()
                    .skip(Math.max(0, syncHistory.size() - 20))
                    .toList();
        }
    }

    public ServiceStats getServiceStats(String serviceName) {
        return serviceStats.getOrDefault(serviceName, new ServiceStats());
    }

    public Map<String, ServiceStats> getAllStats() {
        return new HashMap<>(serviceStats);
    }

    public void reset() {
        syncHistory.clear();
        serviceStats.clear();
    }

    public static class SyncEvent {
        private final String serviceName;
        private final LocalDateTime timestamp;
        private final long durationMs;
        private final boolean success;

        public SyncEvent(String serviceName, LocalDateTime timestamp, long durationMs, boolean success) {
            this.serviceName = serviceName;
            this.timestamp = timestamp;
            this.durationMs = durationMs;
            this.success = success;
        }

        public String getServiceName() { return serviceName; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public long getDurationMs() { return durationMs; }
        public boolean isSuccess() { return success; }
    }

    public static class ServiceStats {
        private int totalSyncs = 0;
        private int successCount = 0;
        private int errorCount = 0;
        private long totalDuration = 0;
        private LocalDateTime lastSync;

        public void recordSync(long durationMs, boolean success) {
            totalSyncs++;
            totalDuration += durationMs;
            lastSync = LocalDateTime.now();

            if (success) {
                successCount++;
            } else {
                errorCount++;
            }
        }

        public int getTotalSyncs() { return totalSyncs; }
        public int getSuccessCount() { return successCount; }
        public int getErrorCount() { return errorCount; }
        public long getAverageDurationMs() {
            return totalSyncs > 0 ? totalDuration / totalSyncs : 0;
        }
        public double getSuccessRate() {
            return totalSyncs > 0 ? (double) successCount / totalSyncs * 100 : 0;
        }
        public LocalDateTime getLastSync() { return lastSync; }
    }
}
