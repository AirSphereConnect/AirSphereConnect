package com.airSphereConnect.dtos.response;

import com.airSphereConnect.entities.enums.ReactionType;

import java.time.LocalDateTime;

public class ForumPostResponseDto {
    private Long id;
    private String content;
    private Long user_id;
    private Long thread_id;
    private ReactionType reaction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ForumPostResponseDto() {
    }

    public ForumPostResponseDto(Long id, String content, Long user_id, Long thread_id, ReactionType reaction, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.user_id = user_id;
        this.thread_id = thread_id;
        this.reaction = reaction;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getThread_id() {
        return thread_id;
    }

    public void setThread_id(Long thread_id) {
        this.thread_id = thread_id;
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
