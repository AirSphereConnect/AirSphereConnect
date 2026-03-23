package com.airsphereconnect.dtos.request;

import com.airsphereconnect.entities.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données reçues lors d'une interaction sur un post")
public class PostReactionRequestDto {

    @Schema(description = "Identifiant de l'utilisateur")
    private Long userId;

    @Schema(description = "Identifiant du post")
    private Long postId;

    @Schema(description = "Le type de réaction du post", example = "Like / Dislike")
    private ReactionType reaction;

    public PostReactionRequestDto() {
    }

    public PostReactionRequestDto(Long userId, Long postId, ReactionType reaction) {
        this.userId = userId;
        this.postId = postId;
        this.reaction = reaction;
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
}
