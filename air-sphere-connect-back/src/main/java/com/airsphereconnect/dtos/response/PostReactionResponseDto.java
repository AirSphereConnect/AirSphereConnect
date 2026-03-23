package com.airsphereconnect.dtos.response;

import com.airsphereconnect.entities.enums.ReactionType;

import java.time.LocalDateTime;

public class PostReactionResponseDto {
    private Long id;
    private Long userId;
    private Long postId;
    private ReactionType reaction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostReactionResponseDto() {
    }

    public PostReactionResponseDto(Long id, Long userId, Long postId, ReactionType reaction, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.reaction = reaction;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public ReactionType getReaction() {
        return reaction;
    }

    public void setReaction(ReactionType reaction) {
        this.reaction = reaction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
