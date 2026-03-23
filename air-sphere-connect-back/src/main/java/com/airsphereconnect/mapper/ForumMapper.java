package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.ForumResponseDto;
import com.airsphereconnect.entities.Forum;
import org.springframework.stereotype.Component;

@Component
public class ForumMapper {


    public ForumResponseDto toResponseDto(Forum forum) {
        if(forum == null) {
            return null;
        }

        ForumResponseDto response = new ForumResponseDto();
        response.setId(forum.getId());
        response.setTitle(forum.getTitle());
        response.setDescription(forum.getDescription());

        return response;
    }
}
