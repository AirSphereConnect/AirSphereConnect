package com.airSphereConnect.dtos.request;

import com.airSphereConnect.entities.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données reçues lors d'une interaction sur un post")
public class PostReactionRequestDto {

    @Schema(description = "Identifiant de l'utilisateur")
    private Long user_id;

    @Schema(description = "Identifiant du post")
    private Long post_id;

    @Schema(description = "Le type de réaction du post", example = "Like / Dislike")
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
