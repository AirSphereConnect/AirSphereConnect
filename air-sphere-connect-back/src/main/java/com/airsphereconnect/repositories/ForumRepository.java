package com.airsphereconnect.repositories;

import com.airsphereconnect.entities.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    Optional<Forum> findById(Long id);

    @Query("SELECT f FROM Forum f LEFT JOIN FETCH f.forumRubrics WHERE f.id = :id")
    Optional<Forum> findByIdWithRubrics(Long id);
}
