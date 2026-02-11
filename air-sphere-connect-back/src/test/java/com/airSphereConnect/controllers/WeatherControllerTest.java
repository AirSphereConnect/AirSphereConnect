package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.WeatherResponseDto;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.airSphereConnect.mapper.WeatherMapper;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.WeatherService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WeatherController Test Suite")
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @MockitoBean
    private WeatherMapper weatherMapper;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private WeatherResponseDto createSampleDto() {
        return new WeatherResponseDto(
                1L, "Montpellier", LocalDateTime.now(),
                22.5, 65.0, 1013.0, 3.5, 180.0,
                null, false, null
        );
    }

    @Test
    @DisplayName("GET /api/weather/latest should return paginated weather data")
    void getLatest_shouldReturn200() throws Exception {
        WeatherMeasurement measurement = new WeatherMeasurement();
        Page<WeatherMeasurement> page = new PageImpl<>(List.of(measurement));
        WeatherResponseDto dto = createSampleDto();

        when(weatherService.findAll(any(Pageable.class))).thenReturn(page);
        when(weatherMapper.toDto(any(WeatherMeasurement.class))).thenReturn(dto);

        mockMvc.perform(get("/api/weather/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/weather/latest with custom pagination")
    void getLatest_withCustomPagination_shouldReturn200() throws Exception {
        Page<WeatherMeasurement> page = new PageImpl<>(List.of());
        when(weatherService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/weather/latest")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /api/weather/city/{id} should return weather for city")
    void getWeatherByCityId_shouldReturn200() throws Exception {
        WeatherMeasurement measurement = new WeatherMeasurement();
        WeatherResponseDto dto = createSampleDto();

        when(weatherService.getWeatherByCityId(1L)).thenReturn(measurement);
        when(weatherMapper.toDto(measurement)).thenReturn(dto);

        mockMvc.perform(get("/api/weather/city/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/weather/city/history/{id} should return weather history")
    void getWeatherHistory_shouldReturn200() throws Exception {
        WeatherMeasurement measurement = new WeatherMeasurement();
        WeatherResponseDto dto = createSampleDto();

        when(weatherService.getWeatherHistoryByCityId(1L)).thenReturn(List.of(measurement));
        when(weatherMapper.toDto(measurement)).thenReturn(dto);

        mockMvc.perform(get("/api/weather/city/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/weather/city/history/{id} should return empty list")
    void getWeatherHistory_shouldReturnEmptyList() throws Exception {
        when(weatherService.getWeatherHistoryByCityId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/weather/city/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
