package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.ForumPost;
import com.airSphereConnect.entities.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    Optional<ForumPost> findByIdAndDeletedAtIsNull(Long id);

    int countByUserIdAndDeletedAtIsNull(Long userId);
    int countByThreadIdAndDeletedAtIsNull(Long threadId);

    @Query("SELECT p FROM ForumPost p " +
            "LEFT JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.thread " +
            "WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<ForumPost> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT p FROM ForumPost p " +
            "LEFT JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.thread " +
            "WHERE p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    List<ForumPost> findAllWithRelations();

    @Query("SELECT p FROM ForumPost p " +
            "LEFT JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.thread " +
            "WHERE p.thread.id = :threadId AND p.deletedAt IS NULL " +
            "ORDER BY p.createdAt ASC")
    List<ForumPost> findByThreadIdWithRelations(@Param("threadId") Long threadId);

    @Query("SELECT p FROM ForumPost p " +
            "LEFT JOIN FETCH p.user " +
            "LEFT JOIN FETCH p.thread " +
            "WHERE p.user.id = :userId AND p.deletedAt IS NULL " +
            "ORDER BY p.createdAt DESC")
    List<ForumPost> findByUserIdWithRelations(@Param("userId") Long userId);

}
