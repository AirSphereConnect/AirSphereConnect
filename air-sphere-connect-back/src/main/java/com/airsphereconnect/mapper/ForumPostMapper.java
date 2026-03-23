package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.request.ForumPostRequestDto;
import com.airsphereconnect.dtos.response.ForumPostResponseDto;
import com.airsphereconnect.entities.ForumPost;
import com.airsphereconnect.entities.ForumThread;
import com.airsphereconnect.entities.User;
import org.springframework.stereotype.Component;

@Component
public class ForumPostMapper {

    public ForumPost toEntity(ForumPostRequestDto request, User user, ForumThread thread) {
        if (request == null) {
            return null;
        }

        ForumPost post = new ForumPost();
        post.setContent(request.getContent());
        post.setUser(user);
        post.setThread(thread);

        return post;
    }

    public ForumPostResponseDto toResponseDto(ForumPost post) {
        if (post == null) {
            return null;
        }

        ForumPostResponseDto response = new ForumPostResponseDto();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setUserId(post.getUser().getId());
        response.setUsername(post.getUser().getUsername());
        response.setUserRole(post.getUser().getRole());
        response.setThreadId(post.getThread().getId());
        response.setThreadTitle(post.getThread().getTitle());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setLikeCount(0);
        response.setDislikeCount(0);
        response.setCurrentUserReaction(null);

        return response;
    }

}
