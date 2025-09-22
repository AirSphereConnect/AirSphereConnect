package com.airSphereConnect.dtos.request;

import com.airSphereConnect.entities.enums.ReactionType;
import jakarta.validation.constraints.NotNull;

public class PostReactionRequestDto {

    private Long user_id;
    private Long post_id;
    private ReactionType reaction;


    public PostReactionRequestDto() {
    }

    public PostReactionRequestDto(Long user_id, Long post_id, ReactionType reaction) {
        this.user_id = user_id;
        this.post_id = post_id;
        this.reaction = reaction;
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
}
