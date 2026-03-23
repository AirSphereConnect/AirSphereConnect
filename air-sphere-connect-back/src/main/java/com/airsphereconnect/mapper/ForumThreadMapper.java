package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.request.ForumThreadRequestDto;
import com.airsphereconnect.dtos.response.ForumThreadResponseDto;
import com.airsphereconnect.entities.ForumRubric;
import com.airsphereconnect.entities.ForumThread;
import com.airsphereconnect.entities.User;
import org.springframework.stereotype.Component;

@Component
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

        return response;
    }
}
