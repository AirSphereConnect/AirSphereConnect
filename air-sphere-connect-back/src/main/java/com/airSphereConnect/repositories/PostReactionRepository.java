package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.PostReaction;
import com.airSphereConnect.entities.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    @Query("SELECT COUNT(pr) FROM PostReaction pr WHERE pr.post.id = :postId AND pr.reactionType = :reactionType AND pr.deletedAt IS NULL")
    long countByPostIdAndReactionType(@Param("postId") Long postId, @Param("reactionType") ReactionType reactionType);

    @Query("SELECT pr.reactionType FROM PostReaction pr WHERE pr.post.id = :postId AND pr.user.id = :userId AND pr.deletedAt IS NULL")
    ReactionType findUserReaction(@Param("postId") Long postId, @Param("userId") Long currentUserId);

    @Query("SELECT pr FROM PostReaction pr WHERE pr.post.id = :postId AND pr.user.id = :userId")
    Optional<PostReaction> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
