package com.airSphereConnect.services;

import com.airSphereConnect.dtos.response.ForumResponseDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;

import java.util.List;

public interface ForumService {
    ForumResponseDto getForumById(Long id);
    List<ForumRubricResponseDto> getRubricsByForumId(Long forumId);

}
