package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.ForumThreadRequestDto;
import com.airSphereConnect.dtos.response.ForumThreadResponseDto;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.ForumThreadService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForumThreadController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ForumThreadController Test Suite")
class ForumThreadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ForumThreadService forumThreadService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/forum-threads should return active threads")
    void getAllActiveThreads_shouldReturn200() throws Exception {
        ForumThreadResponseDto dto = new ForumThreadResponseDto();
        dto.setId(1L);
        dto.setTitle("Thread test");
        when(forumThreadService.getAllActiveThreads()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/forum-threads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Thread test"));
    }

    @Test
    @DisplayName("GET /api/forum-threads/{id} should return thread")
    void getThreadById_shouldReturn200() throws Exception {
        ForumThreadResponseDto dto = new ForumThreadResponseDto();
        dto.setId(1L);
        dto.setTitle("Thread test");
        when(forumThreadService.getThreadById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/forum-threads/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Thread test"));
    }

    @Test
    @DisplayName("GET /api/forum-threads/user/{userId} should return user threads")
    void getThreadsByUser_shouldReturn200() throws Exception {
        ForumThreadResponseDto dto = new ForumThreadResponseDto();
        dto.setId(1L);
        when(forumThreadService.getThreadsByCurrentUser(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/forum-threads/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("POST /api/forum-threads/new/{userId} should create thread")
    void createThread_shouldReturn201() throws Exception {
        ForumThreadRequestDto request = new ForumThreadRequestDto();
        request.setTitle("New Thread");

        ForumThreadResponseDto response = new ForumThreadResponseDto();
        response.setId(1L);
        response.setTitle("New Thread");

        when(forumThreadService.createThread(any(ForumThreadRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/api/forum-threads/new/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Thread"));
    }

    @Test
    @DisplayName("PUT /api/forum-threads/{id} should update thread")
    void updateThread_shouldReturn200() throws Exception {
        ForumThreadRequestDto request = new ForumThreadRequestDto();
        request.setTitle("Updated Thread");

        ForumThreadResponseDto response = new ForumThreadResponseDto();
        response.setId(1L);
        response.setTitle("Updated Thread");

        when(forumThreadService.updateThread(eq(1L), any(ForumThreadRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(put("/api/forum-threads/1")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Thread"));
    }

    @Test
    @DisplayName("DELETE /api/forum-threads/{id} should delete thread")
    void deleteThread_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/forum-threads/1")
                        .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(forumThreadService).deleteThread(1L, 1L);
    }
}
