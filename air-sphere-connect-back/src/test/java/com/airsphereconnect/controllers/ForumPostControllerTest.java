package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.request.ForumPostRequestDto;
import com.airsphereconnect.dtos.response.ForumPostResponseDto;
import com.airsphereconnect.entities.enums.ReactionType;
import com.airsphereconnect.services.CustomUserDetailsService;
import com.airsphereconnect.services.ForumPostService;
import com.airsphereconnect.services.PostReactionService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForumPostController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ForumPostController Test Suite")
class ForumPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ForumPostService forumPostService;

    @MockitoBean
    private PostReactionService postReactionService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/forum-posts should return active posts")
    void getAllActivePosts_shouldReturn200() throws Exception {
        ForumPostResponseDto dto = new ForumPostResponseDto();
        dto.setId(1L);
        dto.setContent("Test post");
        when(forumPostService.getAllActivePosts()).thenReturn(List.of(dto));
        when(postReactionService.countLikesByPost(1L)).thenReturn(5L);
        when(postReactionService.countDislikesByPost(1L)).thenReturn(1L);

        mockMvc.perform(get("/api/forum-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test post"));
    }

    @Test
    @DisplayName("GET /api/forum-posts with currentUserId should enrich reactions")
    void getAllActivePosts_withUserId_shouldEnrich() throws Exception {
        ForumPostResponseDto dto = new ForumPostResponseDto();
        dto.setId(1L);
        when(forumPostService.getAllActivePosts()).thenReturn(List.of(dto));
        when(postReactionService.countLikesByPost(1L)).thenReturn(3L);
        when(postReactionService.countDislikesByPost(1L)).thenReturn(0L);
        when(postReactionService.getUserReaction(1L, 1L)).thenReturn(ReactionType.LIKE);

        mockMvc.perform(get("/api/forum-posts").param("currentUserId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/forum-posts/{id} should return post with reactions")
    void getPostById_shouldReturn200() throws Exception {
        ForumPostResponseDto dto = new ForumPostResponseDto();
        dto.setId(1L);
        dto.setContent("Test post");
        when(forumPostService.getPostById(1L)).thenReturn(dto);
        when(postReactionService.countLikesByPost(1L)).thenReturn(2L);
        when(postReactionService.countDislikesByPost(1L)).thenReturn(0L);

        mockMvc.perform(get("/api/forum-posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Test post"));
    }

    @Test
    @DisplayName("GET /api/forum-posts/user/{userId} should return user posts")
    void getPostsByUser_shouldReturn200() throws Exception {
        ForumPostResponseDto dto = new ForumPostResponseDto();
        dto.setId(1L);
        when(forumPostService.getPostsByUserId(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/forum-posts/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("POST /api/forum-posts/new/{userId} should create post")
    void createPost_shouldReturn201() throws Exception {
        ForumPostRequestDto request = new ForumPostRequestDto();
        request.setContent("New post content");

        ForumPostResponseDto response = new ForumPostResponseDto();
        response.setId(1L);
        response.setContent("New post content");

        when(forumPostService.createPost(any(ForumPostRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/api/forum-posts/new/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("New post content"));
    }

    @Test
    @DisplayName("POST /api/forum-posts/{postId}/reaction should toggle reaction")
    void toggleReaction_shouldReturn200() throws Exception {
        ForumPostResponseDto dto = new ForumPostResponseDto();
        dto.setId(1L);
        when(forumPostService.getPostById(1L)).thenReturn(dto);
        when(postReactionService.countLikesByPost(1L)).thenReturn(1L);
        when(postReactionService.countDislikesByPost(1L)).thenReturn(0L);
        when(postReactionService.getUserReaction(1L, 1L)).thenReturn(ReactionType.LIKE);

        mockMvc.perform(post("/api/forum-posts/1/reaction")
                        .param("userId", "1")
                        .param("reaction", "LIKE"))
                .andExpect(status().isOk());

        verify(postReactionService).toggleReaction(1L, 1L, ReactionType.LIKE);
    }

    @Test
    @DisplayName("PUT /api/forum-posts/{id} should update post")
    void updatePost_shouldReturn200() throws Exception {
        ForumPostRequestDto request = new ForumPostRequestDto();
        request.setContent("Updated content");

        ForumPostResponseDto response = new ForumPostResponseDto();
        response.setId(1L);
        response.setContent("Updated content");

        when(forumPostService.updatePost(eq(1L), any(ForumPostRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(put("/api/forum-posts/1")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    @DisplayName("DELETE /api/forum-posts/{id} should delete post")
    void deletePost_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/forum-posts/1")
                        .param("userId", "1"))
                .andExpect(status().isNoContent());

        verify(forumPostService).deletePost(1L, 1L);
    }
}
