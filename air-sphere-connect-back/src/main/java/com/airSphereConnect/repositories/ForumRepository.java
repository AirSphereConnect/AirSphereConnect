package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {
    Optional<Forum> findById(Long id);

    Optional<Forum> findByTitle(String title);

    @Query("SELECT f FROM Forum f LEFT JOIN FETCH f.forumRubrics WHERE f.id = :id")
    Optional<Forum> findByIdWithRubrics(Long id);
}
