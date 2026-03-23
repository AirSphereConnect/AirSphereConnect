package com.airsphereconnect.dtos.request;

import com.airsphereconnect.entities.enums.ReactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PostReactionRequestDto Test Suite")
class PostReactionRequestDtoTest {

    @Test
    @DisplayName("Devrait créer un DTO avec le constructeur par défaut")
    void shouldCreateWithDefaultConstructor() {
        PostReactionRequestDto dto = new PostReactionRequestDto();
        assertNull(dto.getUserId());
        assertNull(dto.getPostId());
        assertNull(dto.getReaction());
    }

    @Test
    @DisplayName("Devrait créer un DTO avec le constructeur paramétré")
    void shouldCreateWithParameterizedConstructor() {
        PostReactionRequestDto dto = new PostReactionRequestDto(1L, 10L, ReactionType.LIKE);
        assertEquals(1L, dto.getUserId());
        assertEquals(10L, dto.getPostId());
        assertEquals(ReactionType.LIKE, dto.getReaction());
    }

    @Test
    @DisplayName("Devrait définir et obtenir userId")
    void shouldSetAndGetUserId() {
        PostReactionRequestDto dto = new PostReactionRequestDto();
        dto.setUserId(42L);
        assertEquals(42L, dto.getUserId());
    }

    @Test
    @DisplayName("Devrait définir et obtenir postId")
    void shouldSetAndGetPostId() {
        PostReactionRequestDto dto = new PostReactionRequestDto();
        dto.setPostId(99L);
        assertEquals(99L, dto.getPostId());
    }

    @Test
    @DisplayName("Devrait définir et obtenir reaction")
    void shouldSetAndGetReaction() {
        PostReactionRequestDto dto = new PostReactionRequestDto();
        dto.setReaction(ReactionType.DISLIKE);
        assertEquals(ReactionType.DISLIKE, dto.getReaction());
    }
}
