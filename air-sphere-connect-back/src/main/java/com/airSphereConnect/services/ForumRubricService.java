package com.airSphereConnect.services;

import com.airSphereConnect.dtos.request.ForumRubricRequestDto;
import com.airSphereConnect.dtos.response.ForumRubricResponseDto;
import com.airSphereConnect.entities.ForumRubric;

import java.util.List;

public interface ForumRubricService {

    ForumRubricResponseDto createRubric(ForumRubricRequestDto request, Long userId);
    ForumRubricResponseDto getRubricById(Long id);
    List<ForumRubricResponseDto> getAllActiveRubrics();
    List<ForumRubricResponseDto> getRubricsByCurrentUser(Long userId);
    List<ForumRubricResponseDto> getRubricsByForumId(Long forumId);

    ForumRubricResponseDto updateRubric(Long id, ForumRubricRequestDto request, Long userId);
    void deleteRubric(Long id, Long userId);


    ForumRubricResponseDto getRubricWithAllDetails(Long id, Long userId);
    int countRubricsByUser(Long userId);
    int countRubricsByForum(Long forumId);

    List<ForumRubric> findByForumIdOrderByCreatedAtAsc(Long forumId);

    List<ForumRubric> findByForumIdOrderByCreatedAtDesc(Long forumId);

    List<ForumRubric> findByUserIdOrderByCreatedAtAsc(Long userId);

    List<ForumRubric> findByUserIdOrderByCreatedAtDesc(Long userId);

}