package com.airsphereconnect.dtos.response;

import com.airsphereconnect.entities.enums.ReactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PostReactionResponseDto Test Suite")
class PostReactionResponseDtoTest {

    @Test
    @DisplayName("Devrait créer un DTO avec le constructeur par défaut")
    void shouldCreateWithDefaultConstructor() {
        PostReactionResponseDto dto = new PostReactionResponseDto();
        assertNull(dto.getId());
        assertNull(dto.getUserId());
        assertNull(dto.getPostId());
        assertNull(dto.getReaction());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("Devrait créer un DTO avec le constructeur paramétré")
    void shouldCreateWithParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        PostReactionResponseDto dto = new PostReactionResponseDto(1L, 2L, 3L, ReactionType.LIKE, now);
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getUserId());
        assertEquals(3L, dto.getPostId());
        assertEquals(ReactionType.LIKE, dto.getReaction());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Devrait définir et obtenir id")
    void shouldSetAndGetId() {
        PostReactionResponseDto dto = new PostReactionResponseDto();
        dto.setId(10L);
        assertEquals(10L, dto.getId());
    }

    @Test
    @DisplayName("Devrait définir et obtenir userId")
    void shouldSetAndGetUserId() {
        PostReactionResponseDto dto = new PostReactionResponseDto();
        dto.setUserId(20L);
        assertEquals(20L, dto.getUserId());
    }

    @Test
    @DisplayName("Devrait définir et obtenir postId")
    void shouldSetAndGetPostId() {
        PostReactionResponseDto dto = new PostReactionResponseDto();
        dto.setPostId(30L);
        assertEquals(30L, dto.getPostId());
    }

    @Test
    @DisplayName("Devrait définir et obtenir reaction")
    void shouldSetAndGetReaction() {
        PostReactionResponseDto dto = new PostReactionResponseDto();
        dto.setReaction(ReactionType.DISLIKE);
        assertEquals(ReactionType.DISLIKE, dto.getReaction());
    }

    @Test
    @DisplayName("Devrait définir et obtenir createdAt")
    void shouldSetAndGetCreatedAt() {
        PostReactionResponseDto dto = new PostReactionResponseDto();
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    @DisplayName("Devrait définir et obtenir updatedAt")
    void shouldSetAndGetUpdatedAt() {
        PostReactionResponseDto dto = new PostReactionResponseDto();
        LocalDateTime now = LocalDateTime.now();
        dto.setUpdatedAt(now);
        assertEquals(now, dto.getUpdatedAt());
    }
}
