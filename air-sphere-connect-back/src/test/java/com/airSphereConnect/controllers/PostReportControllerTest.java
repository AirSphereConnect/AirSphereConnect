package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.PostReportRequestDto;
import com.airSphereConnect.dtos.response.PostReportResponseDto;
import com.airSphereConnect.entities.enums.ReportReason;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.PostReportService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostReportController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("PostReportController Test Suite")
class PostReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostReportService postReportService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final PostReportResponseDto sampleReport = new PostReportResponseDto(
            1L, 10L, 1L, ReportReason.SPAM, "Test report", "PENDING", LocalDateTime.now());

    @Test
    @DisplayName("POST /api/post-reports/new/{userId} should create report")
    void createReport_shouldReturn201() throws Exception {
        PostReportRequestDto request = new PostReportRequestDto(10L, ReportReason.SPAM, "Test report");
        when(postReportService.createReport(any(PostReportRequestDto.class), eq(1L))).thenReturn(sampleReport);

        mockMvc.perform(post("/api/post-reports/new/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/post-reports/{id} should return report")
    void getReportById_shouldReturn200() throws Exception {
        when(postReportService.getReportById(1L)).thenReturn(sampleReport);

        mockMvc.perform(get("/api/post-reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/post-reports/post/{postId} should return reports for post")
    void getReportsByPost_shouldReturn200() throws Exception {
        when(postReportService.getReportsByPostId(10L)).thenReturn(List.of(sampleReport));

        mockMvc.perform(get("/api/post-reports/post/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value(10));
    }

    @Test
    @DisplayName("GET /api/post-reports/user/{userId} should return user reports")
    void getReportsByUser_shouldReturn200() throws Exception {
        when(postReportService.getReportsByUserId(1L)).thenReturn(List.of(sampleReport));

        mockMvc.perform(get("/api/post-reports/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    @DisplayName("GET /api/post-reports should return reports by status")
    void getReportsByStatus_shouldReturn200() throws Exception {
        when(postReportService.getReportsByStatus("PENDING")).thenReturn(List.of(sampleReport));

        mockMvc.perform(get("/api/post-reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("PUT /api/post-reports/{id}/status should update status")
    void updateReportStatus_shouldReturn200() throws Exception {
        PostReportResponseDto updated = new PostReportResponseDto(
                1L, 10L, 1L, ReportReason.SPAM, "Test report", "RESOLVED", LocalDateTime.now());
        when(postReportService.updateReportStatus(1L, "RESOLVED", 2L)).thenReturn(updated);

        mockMvc.perform(put("/api/post-reports/1/status")
                        .param("status", "RESOLVED")
                        .param("adminId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    @DisplayName("DELETE /api/post-reports/{id} should delete report")
    void deleteReport_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/post-reports/1")
                        .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(postReportService).deleteReport(1L, 1L);
    }

    @Test
    @DisplayName("GET /api/post-reports/check should check if user reported post")
    void checkReport_shouldReturn200() throws Exception {
        when(postReportService.hasUserReportedPost(10L, 1L)).thenReturn(true);

        mockMvc.perform(get("/api/post-reports/check")
                        .param("postId", "10")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
