package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.services.AlertsService;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertsController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AlertsController Test Suite")
class AlertsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlertsService alertsService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/admin/alerts/register should send alerts")
    void sendAlerts_shouldReturn200() throws Exception {
        AlertsDto dto = new AlertsDto();
        dto.setMessage("Pollution alert");

        mockMvc.perform(post("/api/admin/alerts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(alertsService).sendAlerts(any(AlertsDto.class));
    }

    @Test
    @DisplayName("GET /api/admin/alerts/user/{userId} should return user alerts")
    void getUserAlerts_shouldReturn200() throws Exception {
        AlertsDto dto = new AlertsDto();
        dto.setMessage("Alert test");
        when(alertsService.getUserAlerts(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/alerts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Alert test"));
    }

    @Test
    @DisplayName("GET /api/admin/alerts/user/{userId} should return empty list")
    void getUserAlerts_shouldReturnEmptyList() throws Exception {
        when(alertsService.getUserAlerts(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/alerts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
