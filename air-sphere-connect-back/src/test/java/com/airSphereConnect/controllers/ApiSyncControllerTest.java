package com.airSphereConnect.controllers;

import com.airSphereConnect.scheduler.ApiDataSyncScheduler;
import com.airSphereConnect.scheduler.SyncMetrics;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiSyncController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ApiSyncController Test Suite")
class ApiSyncControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApiDataSyncScheduler scheduler;

    @MockitoBean
    private SyncMetrics syncMetrics;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/admin/sync/force-all should sync all services")
    void forceSyncAll_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/admin/sync/force-all"))
                .andExpect(status().isOk());

        verify(scheduler).forceSyncAll();
    }

    @Test
    @DisplayName("POST /api/admin/sync/force-all should return 500 on error")
    void forceSyncAll_shouldReturn500OnError() throws Exception {
        doThrow(new RuntimeException("Sync failed")).when(scheduler).forceSyncAll();

        mockMvc.perform(post("/api/admin/sync/force-all"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/admin/sync/force/{serviceName} should sync specific service")
    void forceSync_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/admin/sync/force/weather"))
                .andExpect(status().isOk());

        verify(scheduler).forceSyncService("weather");
    }

    @Test
    @DisplayName("POST /api/admin/sync/force/{serviceName} should return 400 for invalid service")
    void forceSync_shouldReturn400ForInvalidService() throws Exception {
        doThrow(new IllegalArgumentException("Service inconnu")).when(scheduler).forceSyncService("invalid");

        mockMvc.perform(post("/api/admin/sync/force/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/admin/sync/force/{serviceName} should return 500 on error")
    void forceSync_shouldReturn500OnError() throws Exception {
        doThrow(new RuntimeException("Error")).when(scheduler).forceSyncService("weather");

        mockMvc.perform(post("/api/admin/sync/force/weather"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("GET /api/admin/sync/metrics should return global metrics")
    void getMetrics_shouldReturn200() throws Exception {
        when(syncMetrics.getRecentHistory()).thenReturn(List.of());
        when(syncMetrics.getAllStats()).thenReturn(Map.of());

        mockMvc.perform(get("/api/admin/sync/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recentHistory").isArray())
                .andExpect(jsonPath("$.serviceStats").isMap());
    }

    @Test
    @DisplayName("GET /api/admin/sync/metrics/{serviceName} should return service metrics")
    void getServiceMetrics_shouldReturn200() throws Exception {
        SyncMetrics.ServiceStats stats = new SyncMetrics.ServiceStats();
        when(syncMetrics.getServiceStats("weather")).thenReturn(stats);

        mockMvc.perform(get("/api/admin/sync/metrics/weather"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSyncs").value(0));
    }
}
