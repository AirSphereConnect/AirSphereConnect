package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.FavoritesAlertsDto;
import com.airsphereconnect.mapper.AddressMapper;
import com.airsphereconnect.mapper.FavoriteMapper;
import com.airsphereconnect.mapper.FavoritesAlertsMapper;
import com.airsphereconnect.mapper.UserMapper;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.FavoritesAlertsService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoritesAlertsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({UserMapper.class, FavoriteMapper.class, FavoritesAlertsMapper.class, AddressMapper.class})
@DisplayName("FavoritesAlertsController Test Suite")
class FavoritesAlertsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FavoritesAlertsService favoritesAlertsService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/alert/configurations should return all configs")
    void getAllAlertConfigs_shouldReturn200() throws Exception {
        FavoritesAlertsDto dto = new FavoritesAlertsDto();
        dto.setId(1L);
        when(favoritesAlertsService.getAllFavoritesAlerts()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/alert/configurations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/alert/configurations/user/{userId} should return user configs")
    void getUserAlertConfigs_shouldReturn200() throws Exception {
        FavoritesAlertsDto dto = new FavoritesAlertsDto();
        dto.setId(1L);
        when(favoritesAlertsService.getUserAlerts(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/alert/configurations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/alert/configurations/{id} should delete config")
    void deleteAlertConfig_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/alert/configurations/1"))
                .andExpect(status().isOk());

        verify(favoritesAlertsService).deleteAlertConfig(1L);
    }
}
