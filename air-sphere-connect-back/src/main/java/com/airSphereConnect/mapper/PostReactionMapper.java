package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.PostReactionRequestDto;
import com.airSphereConnect.dtos.response.PostReactionResponseDto;
import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.PostReaction;
import com.airSphereConnect.entities.User;

public class PostReactionMapper {

    public PostReaction toEntity(PostReactionRequestDto request, User user, ForumPost post) {
        if (request == null) {
            return null;
        }

        PostReaction postReaction = new PostReaction();
        postReaction.setUser(user);
        postReaction.setPost(post);
        postReaction.setReactionType(request.getReaction());

        return postReaction;

    }

    public PostReactionResponseDto toResponseDto(PostReaction postReaction) {
        if (postReaction == null) {
            return null;
        }

        PostReactionResponseDto response = new PostReactionResponseDto();
        response.setId(postReaction.getId());
        response.setUser_id(postReaction.getUser().getId());
        response.setPost_id(postReaction.getPost().getId());
        response.setReaction(postReaction.getReactionType());
        response.setCreated_at(postReaction.getCreatedAt());
        response.setUpdated_at(postReaction.getUpdatedAt());

        return response;
    }
}
