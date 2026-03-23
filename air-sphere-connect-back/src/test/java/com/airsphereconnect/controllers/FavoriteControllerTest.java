package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.FavoriteDto;
import com.airsphereconnect.mapper.AddressMapper;
import com.airsphereconnect.mapper.FavoriteMapper;
import com.airsphereconnect.mapper.FavoritesAlertsMapper;
import com.airsphereconnect.mapper.UserMapper;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.FavoriteService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({UserMapper.class, FavoriteMapper.class, FavoritesAlertsMapper.class, AddressMapper.class})
@DisplayName("FavoriteController Test Suite")
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FavoriteService favoriteService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/favorites should return all favorites")
    void getAllFavorites_shouldReturn200() throws Exception {
        FavoriteDto dto = new FavoriteDto();
        dto.setId(1L);
        when(favoriteService.getAllFavorites()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/favorites/{id} should return favorite")
    void getFavoriteById_shouldReturn200() throws Exception {
        FavoriteDto dto = new FavoriteDto();
        dto.setId(1L);
        when(favoriteService.getFavoriteById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/favorites/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/favorites/{id} should update favorite")
    void updateFavorite_shouldReturn200() throws Exception {
        FavoriteDto dto = new FavoriteDto();
        dto.setId(1L);
        dto.setSelectWeather(false);

        when(favoriteService.updateFavorite(eq(1L), any(FavoriteDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/favorites/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/favorites/{id} should delete favorite")
    void deleteFavorite_shouldReturn200() throws Exception {
        FavoriteDto dto = new FavoriteDto();
        dto.setId(1L);
        when(favoriteService.deleteFavorite(1L)).thenReturn(dto);

        mockMvc.perform(delete("/api/favorites/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
