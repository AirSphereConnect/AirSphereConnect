package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.response.AirQualityDataResponseDto;
import com.airsphereconnect.dtos.response.AirQualityIndexResponseDto;
import com.airsphereconnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airsphereconnect.dtos.response.AirQualityStationResponseDto;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.implementations.AirQualityServiceImpl;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AirQualityController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AirQualityController Test Suite")
class AirQualityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AirQualityServiceImpl airQualityService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("GET /stations should return list of stations")
    void getAllStations_shouldReturn200() throws Exception {
        AirQualityStationResponseDto station = new AirQualityStationResponseDto(
                1L, "Station Montpellier", "ATMO-34", "243400017", "Montpellier");

        when(airQualityService.getAllStations()).thenReturn(List.of(station));

        mockMvc.perform(get("/api/air-quality/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Station Montpellier"))
                .andExpect(jsonPath("$[0].city").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /stations should return empty list when no stations")
    void getAllStations_shouldReturnEmptyList() throws Exception {
        when(airQualityService.getAllStations()).thenReturn(List.of());

        mockMvc.perform(get("/api/air-quality/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /city/{name}/latest-measurement should return measurement")
    void getLatestMeasurement_shouldReturn200() throws Exception {
        AirQualityMeasurementResponseDto dto = new AirQualityMeasurementResponseDto(
                1L, 25.0, 15.0, 30.0, 50.0, 5.0, "µg/m³", now, "Station", "exact", null);

        when(airQualityService.getLatestMeasurementForCity("Montpellier")).thenReturn(dto);

        mockMvc.perform(get("/api/air-quality/city/Montpellier/latest-measurement"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pm10").value(25.0))
                .andExpect(jsonPath("$.dataSource").value("exact"));
    }

    @Test
    @DisplayName("GET /city/{name}/latest-index should return index")
    void getLatestIndex_shouldReturn200() throws Exception {
        AirQualityIndexResponseDto dto = new AirQualityIndexResponseDto(
                1L, 3, "Moyen", "#FFFF00", now, "243400017", "Montpellier",
                "ATMO Occitanie", "Qualité moyenne");

        when(airQualityService.getLatestIndexQualityForCity("Montpellier")).thenReturn(dto);

        mockMvc.perform(get("/api/air-quality/city/Montpellier/latest-index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qualityIndex").value(3))
                .andExpect(jsonPath("$.qualityLabel").value("Moyen"));
    }

    @Test
    @DisplayName("GET /city/{name}/complete should return complete data")
    void getCompleteData_shouldReturn200() throws Exception {
        AirQualityDataResponseDto dto = new AirQualityDataResponseDto();
        dto.setCityId(1L);
        dto.setCityName("Montpellier");
        dto.setQualityIndex(3);
        dto.setPm10(25.0);

        when(airQualityService.getCompleteDataForCity("Montpellier")).thenReturn(dto);

        mockMvc.perform(get("/api/air-quality/city/Montpellier/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cityName").value("Montpellier"))
                .andExpect(jsonPath("$.qualityIndex").value(3))
                .andExpect(jsonPath("$.pm10").value(25.0));
    }

    @Test
    @DisplayName("GET /city/{name}/history/measurements should return history")
    void getMeasurementsHistory_shouldReturn200() throws Exception {
        AirQualityMeasurementResponseDto dto = new AirQualityMeasurementResponseDto(
                1L, 25.0, 15.0, 30.0, 50.0, 5.0, "µg/m³", now, "Station", "exact", null);

        when(airQualityService.getMeasurementsHistoryForCity(eq("Montpellier"), any(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/air-quality/city/Montpellier/history/measurements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pm10").value(25.0));
    }

    @Test
    @DisplayName("GET /city/{name}/history/indices should return history")
    void getIndicesHistory_shouldReturn200() throws Exception {
        AirQualityIndexResponseDto dto = new AirQualityIndexResponseDto(
                1L, 3, "Moyen", "#FFFF00", now, "243400017", "Montpellier",
                "ATMO Occitanie", null);

        when(airQualityService.getIndicesHistoryForCity(eq("Montpellier"), any(), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/air-quality/city/Montpellier/history/indices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].qualityIndex").value(3));
    }

    @Test
    @DisplayName("GET /department/{code}/top-cities should return top cities")
    void getTopCities_shouldReturn200() throws Exception {
        AirQualityDataResponseDto dto = new AirQualityDataResponseDto();
        dto.setCityName("Montpellier");

        when(airQualityService.getTopCitiesWithDataInDepartment("34", 2))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/air-quality/department/34/top-cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cityName").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /department/{code}/top-cities with custom limit")
    void getTopCities_shouldAcceptCustomLimit() throws Exception {
        when(airQualityService.getTopCitiesWithDataInDepartment("34", 5))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/air-quality/department/34/top-cities")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
