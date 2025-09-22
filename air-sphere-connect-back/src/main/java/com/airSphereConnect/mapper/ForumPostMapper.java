package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.ForumPostRequestDto;
import com.airSphereConnect.dtos.response.ForumPostResponseDto;
import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.ForumThread;
import com.airSphereConnect.entities.PostReaction;
import com.airSphereConnect.entities.User;
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
