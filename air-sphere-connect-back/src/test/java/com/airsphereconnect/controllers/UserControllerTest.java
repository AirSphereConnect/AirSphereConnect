package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.request.UserRequestDto;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.mapper.AddressMapper;
import com.airsphereconnect.mapper.FavoriteMapper;
import com.airsphereconnect.mapper.FavoritesAlertsMapper;
import com.airsphereconnect.mapper.UserMapper;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.AuthService;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.UserService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({UserMapper.class, FavoriteMapper.class, FavoritesAlertsMapper.class, AddressMapper.class})
@DisplayName("UserController Test Suite")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/users should return all users")
    void getAllUsers_shouldReturn200() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/users/check should check availability")
    void checkAvailability_shouldReturn200() throws Exception {
        when(userService.existsByUsername("taken")).thenReturn(true);
        when(userService.existsByEmail("free@test.com")).thenReturn(false);

        mockMvc.perform(get("/api/users/check")
                        .param("username", "taken")
                        .param("email", "free@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usernameTaken").value(true))
                .andExpect(jsonPath("$.emailTaken").value(false));
    }

    @Test
    @DisplayName("GET /api/users/name should return user by username")
    void getUserByUsername_shouldReturn200() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        mockMvc.perform(get("/api/users/name").param("username", "testuser"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/users/id should return user by ID")
    void getUserById_shouldReturn200() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/id").param("id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/users/signup should create user")
    void signup_shouldReturn200() throws Exception {
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername("newuser");
        doReturn(ResponseEntity.ok(Map.of("username", "newuser"))).when(authService).signupAndLogin(any(), any());

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/users should delete user")
    void deleteUser_shouldReturn204() throws Exception {
        when(authService.deleteUser(eq(1L), any(), any())).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/api/users").param("id", "1"))
                .andExpect(status().isNoContent());
    }
}
