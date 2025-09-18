package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.entities.ForumRubric;
import org.springframework.stereotype.Component;

@Component
public class ForumRubricMapper {

    public ForumRubric toEntity(ForumRubricRequestDto request) {
        if (request == null) {
            return null;
        }

        ForumRubric rubric = new ForumRubric();
        rubric.setTitle(request.getTitle());
        rubric.setDescription(request.getDescription());
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
        response.setCreatedAt(rubric.getCreatedAt());
        response.setUpdatedAt(rubric.getUpdatedAt());

        return response;
    }


}
