package com.airSphereConnect.services;

import com.airSphereConnect.dtos.request.ForumPostRequestDto;
import com.airSphereConnect.dtos.response.ForumPostResponseDto;

import java.util.List;

public interface ForumPostService {

    ForumPostResponseDto getPostById(Long id);
    List<ForumPostResponseDto> getAllActivePosts();
    List<ForumPostResponseDto> getPostsByThreadId(Long threadId);
    List<ForumPostResponseDto> getPostsByUserId(Long userId);

    ForumPostResponseDto createPost(ForumPostRequestDto request, Long userId);
    ForumPostResponseDto updatePost(Long id, ForumPostRequestDto request, Long userId);
    void deletePost(Long id, Long userId);

}
