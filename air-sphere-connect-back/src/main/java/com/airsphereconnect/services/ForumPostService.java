package com.airsphereconnect.services;

import com.airsphereconnect.dtos.request.ForumPostRequestDto;
import com.airsphereconnect.dtos.response.ForumPostResponseDto;

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
