package com.airSphereConnect.services;

import com.airSphereConnect.dtos.response.ForumPostResponseDto;
import com.airSphereConnect.entities.enums.ReactionType;

public interface PostReactionService {

    void toggleReaction(Long postId, Long userId, ReactionType reaction);
    long countLikesByPost(Long postId);
    long countDislikesByPost(Long postId);
    ReactionType getUserReaction(Long postId, Long userId);
}
