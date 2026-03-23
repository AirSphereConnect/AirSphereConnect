package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.response.RegionResponseDto;
import com.airsphereconnect.entities.Region;
import com.airsphereconnect.mapper.RegionMapper;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.RegionService;
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

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RegionController Test Suite")
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegionService regionService;

    @MockitoBean
    private RegionMapper regionMapper;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/regions should return list of regions")
    void getAllRegions_shouldReturn200() throws Exception {
        Region region = new Region();
        RegionResponseDto dto = new RegionResponseDto(1L, "Occitanie", "76");

        when(regionService.getAllRegions()).thenReturn(List.of(region));
        when(regionMapper.toDto(region)).thenReturn(dto);

        mockMvc.perform(get("/api/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Occitanie"))
                .andExpect(jsonPath("$[0].code").value("76"));
    }

    @Test
    @DisplayName("GET /api/regions/regionName/{name} should return region")
    void getRegionByName_shouldReturn200() throws Exception {
        Region region = new Region();
        RegionResponseDto dto = new RegionResponseDto(1L, "Occitanie", "76");

        when(regionService.getRegionByName("Occitanie")).thenReturn(region);
        when(regionMapper.toDto(region)).thenReturn(dto);

        mockMvc.perform(get("/api/regions/regionName/Occitanie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Occitanie"));
    }

    @Test
    @DisplayName("GET /api/regions/regionCode/{code} should return region")
    void getRegionByCode_shouldReturn200() throws Exception {
        Region region = new Region();
        RegionResponseDto dto = new RegionResponseDto(1L, "Occitanie", "76");

        when(regionService.getRegionByCode("76")).thenReturn(region);
        when(regionMapper.toDto(region)).thenReturn(dto);

        mockMvc.perform(get("/api/regions/regionCode/76"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("76"));
    }

    @Test
    @DisplayName("GET /api/regions should return empty list")
    void getAllRegions_shouldReturnEmptyList() throws Exception {
        when(regionService.getAllRegions()).thenReturn(List.of());

        mockMvc.perform(get("/api/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
