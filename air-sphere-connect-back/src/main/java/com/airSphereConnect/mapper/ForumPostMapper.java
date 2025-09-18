package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.ForumPostRequestDto;
import com.airSphereConnect.dtos.response.ForumPostResponseDto;
import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.ForumThread;
import com.airSphereConnect.entities.PostReaction;
import com.airSphereConnect.entities.User;

public class ForumPostMapper {

    public ForumPost toEntity(ForumPostRequestDto request, User user, ForumThread thread) {
        if (request == null) {
            return null;
        }

        ForumPost forumPost = new ForumPost();
        forumPost.setContent(request.getContent());
        forumPost.setUser(user);
        forumPost.setThread(thread);


       return forumPost;
    }

    public ForumPostResponseDto toResponseDto(ForumPost forumPost) {
        if (forumPost == null) {
            return null;
        }

        ForumPostResponseDto response = new ForumPostResponseDto();
        response.setId(forumPost.getId());
        response.setContent(forumPost.getContent());
        response.setUser_id(forumPost.getUser().getId());
        response.setThread_id(forumPost.getThread().getId());
        response.setCreatedAt(forumPost.getCreatedAt());
        response.setUpdatedAt(forumPost.getUpdatedAt());

        return response;
    }

}
