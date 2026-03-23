package com.airsphereconnect.services;

import com.airsphereconnect.entities.enums.ReactionType;

public interface PostReactionService {

    void toggleReaction(Long postId, Long userId, ReactionType reaction);
    long countLikesByPost(Long postId);
    long countDislikesByPost(Long postId);
    ReactionType getUserReaction(Long postId, Long userId);
}
