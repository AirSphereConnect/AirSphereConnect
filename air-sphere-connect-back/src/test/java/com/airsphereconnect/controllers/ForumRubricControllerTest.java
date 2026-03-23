package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.request.ForumRubricRequestDto;
import com.airsphereconnect.dtos.response.ForumRubricResponseDto;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.ForumRubricService;
import com.airsphereconnect.services.security.implementations.JwtServiceImpl;
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

@WebMvcTest(ForumRubricController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ForumRubricController Test Suite")
class ForumRubricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ForumRubricService forumRubricService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/forum-rubrics should return active rubrics")
    void getAllActiveRubrics_shouldReturn200() throws Exception {
        ForumRubricResponseDto dto = new ForumRubricResponseDto();
        dto.setId(1L);
        dto.setTitle("Rubric test");
        when(forumRubricService.getAllActiveRubrics()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/forum-rubrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Rubric test"));
    }

    @Test
    @DisplayName("GET /api/forum-rubrics/{id} should return rubric")
    void getRubricById_shouldReturn200() throws Exception {
        ForumRubricResponseDto dto = new ForumRubricResponseDto();
        dto.setId(1L);
        dto.setTitle("Rubric test");
        when(forumRubricService.getRubricById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/forum-rubrics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Rubric test"));
    }

    @Test
    @DisplayName("GET /api/forum-rubrics/user/{userId} should return user rubrics")
    void getRubricsByUser_shouldReturn200() throws Exception {
        ForumRubricResponseDto dto = new ForumRubricResponseDto();
        dto.setId(1L);
        when(forumRubricService.getRubricsByCurrentUser(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/forum-rubrics/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("POST /api/forum-rubrics/new/{userId} should create rubric")
    void createRubric_shouldReturn201() throws Exception {
        ForumRubricRequestDto request = new ForumRubricRequestDto();
        request.setTitle("New Rubric");
        request.setDescription("Description test");

        ForumRubricResponseDto response = new ForumRubricResponseDto();
        response.setId(1L);
        response.setTitle("New Rubric");

        when(forumRubricService.createRubric(any(ForumRubricRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/api/forum-rubrics/new/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Rubric"));
    }

    @Test
    @DisplayName("PUT /api/forum-rubrics/{id} should update rubric")
    void updateRubric_shouldReturn200() throws Exception {
        ForumRubricRequestDto request = new ForumRubricRequestDto();
        request.setTitle("Updated Rubric");
        request.setDescription("Updated description");

        ForumRubricResponseDto response = new ForumRubricResponseDto();
        response.setId(1L);
        response.setTitle("Updated Rubric");

        when(forumRubricService.updateRubric(eq(1L), any(ForumRubricRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(put("/api/forum-rubrics/1")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Rubric"));
    }

    @Test
    @DisplayName("DELETE /api/forum-rubrics/{id} should delete rubric")
    void deleteRubric_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/forum-rubrics/1")
                        .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(forumRubricService).deleteRubric(1L, 1L);
    }
}
