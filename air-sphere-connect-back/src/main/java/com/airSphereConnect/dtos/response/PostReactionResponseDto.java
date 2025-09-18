package com.airSphereConnect.dtos.response;

import com.airSphereConnect.entities.enums.ReactionType;

import java.time.LocalDateTime;

public class PostReactionResponseDto {
    private Long id;
    private Long user_id;
    private Long post_id;
    private ReactionType reaction;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public PostReactionResponseDto() {
    }

    public PostReactionResponseDto(Long id, Long user_id, Long post_id, ReactionType reaction, LocalDateTime created_at) {
        this.id = id;
        this.user_id = user_id;
        this.post_id = post_id;
        this.reaction = reaction;
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getPost_id() {
        return post_id;
    }

    public void setPost_id(Long post_id) {
        this.post_id = post_id;
    }

    public ReactionType getReaction() {
        return reaction;
    }

    public void setReaction(ReactionType reaction) {
        this.reaction = reaction;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
