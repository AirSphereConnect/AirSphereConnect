package com.airSphereConnect.services.api;

import java.time.Duration;
import java.time.LocalDateTime;

public interface DataSyncService {
    String getServiceName();
    void syncData();
    boolean isEnabled();
    Duration getSyncInterval();
    LocalDateTime getLastSync();
    int getConsecutiveErrors();
}
