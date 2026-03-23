package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.request.LoginRequestDto;
import com.airsphereconnect.services.AuthService;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HomeController Test Suite")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/login should delegate to authService")
    void login_shouldReturn200() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("user", "password");
        when(authService.login(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/profile should delegate to authService")
    void getProfile_shouldReturn200() throws Exception {
        when(authService.getUserProfile(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/logout should delegate to authService")
    void logout_shouldReturn200() throws Exception {
        when(authService.logout(any(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/api/logout"))
                .andExpect(status().isOk());
    }
}
