package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.response.PopulationResponseDto;
import com.airsphereconnect.entities.Population;
import com.airsphereconnect.mapper.PopulationMapper;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.PopulationService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PopulationController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PopulationController Test Suite")
class PopulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PopulationService populationService;

    @MockitoBean
    private PopulationMapper populationMapper;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/history/{cityName} should return population history")
    void getHistoryByCityName_shouldReturn200() throws Exception {
        Population pop = new Population();
        PopulationResponseDto dto = new PopulationResponseDto(50000, 2023, "INSEE");

        when(populationService.getHistoryByCityName("Toulouse")).thenReturn(List.of(pop));
        when(populationMapper.toDto(pop)).thenReturn(dto);

        mockMvc.perform(get("/api/history/Toulouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].population").value(50000))
                .andExpect(jsonPath("$[0].year").value(2023));
    }

    @Test
    @DisplayName("GET /api/history/{cityName} should return empty list")
    void getHistoryByCityName_shouldReturnEmptyList() throws Exception {
        when(populationService.getHistoryByCityName("Unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/history/Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
