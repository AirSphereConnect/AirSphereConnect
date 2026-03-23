package com.airsphereconnect.controllers;

import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.api.WeatherSyncService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController Test Suite")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherSyncService weatherSyncService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/admin/refresh-weather should refresh weather data")
    void refreshWeather_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/admin/refresh-weather"))
                .andExpect(status().isOk())
                .andExpect(content().string("Météo mise à jour manuellement"));

        verify(weatherSyncService).fetchAndStoreWeatherForAllCities();
    }
}
