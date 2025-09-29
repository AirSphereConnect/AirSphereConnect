package com.airSphereConnect.services;

import com.airSphereConnect.dtos.request.ForumThreadRequestDto;
import com.airSphereConnect.dtos.response.ForumThreadResponseDto;

import java.util.List;

public interface ForumThreadService {
    ForumThreadResponseDto createThread(ForumThreadRequestDto request, Long userId);
    ForumThreadResponseDto getThreadById(Long id);
    List<ForumThreadResponseDto> getAllActiveThreads();
    List<ForumThreadResponseDto> getThreadsByCurrentUser(Long userId);
    List<ForumThreadResponseDto> getThreadsByForumRubricId(Long rubricId);

    ForumThreadResponseDto updateThread(Long id, ForumThreadRequestDto request, Long userId);
    void deleteThread(Long id, Long userId);

    ForumThreadResponseDto getThreadWithAllDetails(Long id, Long userId);

    int countThreadsByUser(Long userId);
    int countThreadsByRubric(Long rubricId);
}
