package com.airsphereconnect.repositories;

import com.airsphereconnect.entities.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {
    Optional<ForumThread> findByIdAndDeletedAtIsNull(Long id);

    List<ForumThread> findAllByDeletedAtIsNull();

    List<ForumThread> findByUserIdAndDeletedAtIsNull(Long userId);

    int countByForumRubricIdAndDeletedAtIsNull(Long rubricId);

    int countByUserIdAndDeletedAtIsNull(Long userId);

    List<ForumThread> findByForumRubricIdAndDeletedAtIsNull(Long rubricId);

    boolean existsByForumRubricIdAndTitleIgnoreCaseAndDeletedAtIsNull(Long rubricId, String title);

}
