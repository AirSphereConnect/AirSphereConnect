package com.airSphereConnect.controllers;

import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.api.HistoricalDataLoaderService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistoricalDataLoaderController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HistoricalDataLoaderController Test Suite")
class HistoricalDataLoaderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistoricalDataLoaderService loaderService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/admin/historical-data/load-30-days should load data successfully")
    void loadLast30Days_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/admin/historical-data/load-30-days"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        verify(loaderService).loadLast30DaysHistory();
    }

    @Test
    @DisplayName("POST /api/admin/historical-data/load-30-days should return 500 on error")
    void loadLast30Days_shouldReturn500OnError() throws Exception {
        doThrow(new RuntimeException("Load failed")).when(loaderService).loadLast30DaysHistory();

        mockMvc.perform(post("/api/admin/historical-data/load-30-days"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("error"));
    }
}
