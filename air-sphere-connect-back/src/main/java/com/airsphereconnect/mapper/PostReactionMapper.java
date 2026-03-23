package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.request.PostReactionRequestDto;
import com.airsphereconnect.dtos.response.PostReactionResponseDto;
import com.airsphereconnect.entities.ForumPost;
import com.airsphereconnect.entities.PostReaction;
import com.airsphereconnect.entities.User;
import org.springframework.stereotype.Component;

@Component
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
        response.setUserId(postReaction.getUser().getId());
        response.setPostId(postReaction.getPost().getId());
        response.setReaction(postReaction.getReactionType());
        response.setCreatedAt(postReaction.getCreatedAt());
        response.setUpdatedAt(postReaction.getUpdatedAt());

        return response;
    }
}
