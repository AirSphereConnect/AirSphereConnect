package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.ExternalAlertDto;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.ExternalAlertProcessingService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExternalAlertController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ExternalAlertController Test Suite")
class ExternalAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExternalAlertProcessingService processingService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/admin/external-alerts/receive should process alert")
    void receiveAlert_shouldReturn200() throws Exception {
        ExternalAlertDto alert = new ExternalAlertDto(1L, "POLLUTION", "High PM2.5 levels");

        mockMvc.perform(post("/api/admin/external-alerts/receive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alert)))
                .andExpect(status().isOk());

        verify(processingService).processExternalAlert(any(ExternalAlertDto.class));
    }
}
