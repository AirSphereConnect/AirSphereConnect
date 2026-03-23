package com.airsphereconnect.services;

import com.airsphereconnect.dtos.response.ForumResponseDto;
import com.airsphereconnect.dtos.response.ForumRubricResponseDto;

import java.util.List;

public interface ForumService {
    ForumResponseDto getForumById(Long id);

    List<ForumRubricResponseDto> getRubricsByForumId(Long id);

}
