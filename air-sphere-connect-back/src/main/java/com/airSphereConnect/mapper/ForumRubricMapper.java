package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.entities.Forum;
import com.airSphereConnect.entities.ForumRubric;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.ForumRepository;
import com.airSphereConnect.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class ForumRubricMapper {

    private final UserRepository userRepository;
    private final ForumRepository forumRepository;

    public ForumRubricMapper(UserRepository userRepository, ForumRepository forumRepository) {
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
    }

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
