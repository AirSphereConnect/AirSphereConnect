package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.ForumResponseDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.ForumRubricService;
import com.airSphereConnect.services.ForumService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
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

@WebMvcTest(ForumController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ForumController Test Suite")
class ForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForumService forumService;

    @MockitoBean
    private ForumRubricService forumRubricService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/forums/{id} should return forum")
    void getForumById_shouldReturn200() throws Exception {
        ForumResponseDto dto = new ForumResponseDto();
        dto.setId(1L);
        dto.setTitle("Forum environnement");
        when(forumService.getForumById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/forums/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Forum environnement"));
    }

    @Test
    @DisplayName("GET /api/forums/{id}/rubrics should return rubrics")
    void getForumRubrics_shouldReturn200() throws Exception {
        ForumRubricResponseDto rubric = new ForumRubricResponseDto();
        rubric.setId(1L);
        rubric.setTitle("Rubric 1");
        when(forumRubricService.getRubricsByForumId(1L)).thenReturn(List.of(rubric));

        mockMvc.perform(get("/api/forums/1/rubrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Rubric 1"));
    }

    @Test
    @DisplayName("GET /api/forums/{id}/rubrics should return empty list")
    void getForumRubrics_shouldReturnEmptyList() throws Exception {
        when(forumRubricService.getRubricsByForumId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/forums/1/rubrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
