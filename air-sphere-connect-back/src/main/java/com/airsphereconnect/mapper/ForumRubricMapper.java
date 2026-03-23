package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.request.ForumRubricRequestDto;
import com.airsphereconnect.dtos.response.ForumRubricResponseDto;
import com.airsphereconnect.entities.Forum;
import com.airsphereconnect.entities.ForumRubric;
import com.airsphereconnect.entities.User;
import org.springframework.stereotype.Component;

@Component
public class ForumRubricMapper {

    public ForumRubric toEntity(ForumRubricRequestDto request, User user, Forum forum) {
        if (request == null) {
            return null;
        }

        ForumRubric rubric = new ForumRubric();
        rubric.setTitle(request.getTitle());
        rubric.setDescription(request.getDescription());
        rubric.setUser(user);
        rubric.setForum(forum);

        return rubric;
    }

    public ForumRubricResponseDto toResponseDto(ForumRubric rubric) {
        if (rubric == null) {
            return null;
        }
        ForumRubricResponseDto response = new ForumRubricResponseDto();
        response.setId(rubric.getId());
        response.setTitle(rubric.getTitle());
        response.setDescription(rubric.getDescription());
        response.setUserId(rubric.getUser().getId());
        response.setUsername(rubric.getUser().getUsername());
        response.setForumId(rubric.getForum().getId());
        response.setForumTitle(rubric.getForum().getTitle());

        return response;
    }


}
