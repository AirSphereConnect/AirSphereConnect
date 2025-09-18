package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.ForumThreadRequestDto;
import com.airSphereConnect.dtos.response.ForumThreadResponseDto;
import com.airSphereConnect.entities.ForumRubric;
import com.airSphereConnect.entities.ForumThread;
import com.airSphereConnect.entities.User;

public class ForumThreadMapper {

    public ForumThread toEntity(ForumThreadRequestDto request, User user, ForumRubric rubric) {
        if (request == null) {
            return null;
        }

        ForumThread thread = new ForumThread();
        thread.setTitle(request.getTitle());
        thread.setUser(user);
        thread.setForumRubric(rubric);

        return thread;
    }

    public ForumThreadResponseDto toResponseDto(ForumThread thread) {
        if (thread == null) {
            return null;
        }

        ForumThreadResponseDto response = new ForumThreadResponseDto();
        response.setId(thread.getId());
        response.setTitle(thread.getTitle());
        response.setUserId(thread.getUser().getId());
        response.setUsername(thread.getUser().getUsername());
        response.setRubricId(thread.getForumRubric().getId());
        response.setRubricTitle(thread.getForumRubric().getTitle());
        response.setCreatedAt(thread.getCreatedAt());
        response.setUpdatedAt(thread.getUpdatedAt());

        return response;
    }
}
