package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.ForumRubric;
import com.airSphereConnect.entities.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {
    Optional<ForumThread> findByIdAndDeletedAtIsNull(Long id);

    List<ForumThread> findAllByDeletedAtIsNull();

    List<ForumThread> findByUserIdAndDeletedAtIsNull(Long userId);

    int countByForumRubricIdAndDeletedAtIsNull(Long forumRubricId);

    int countByUserIdAndDeletedAtIsNull(Long userId);

    List<ForumThread> findByForumRubricIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long rubricId);
    List<ForumThread> findByForumRubricIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long rubricId);

    List<ForumThread> findByUserIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long userId);
    List<ForumThread> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);

    List<ForumThread> findByForumRubricIdAndDeletedAtIsNull(Long rubricId);

    @Query("SELECT t FROM ForumThread t " +
            "LEFT JOIN FETCH t.forumRubric " +
            "LEFT JOIN FETCH t.user " +
            "WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<ForumThread> findByIdWithRelations(@Param("id") Long id);
}
