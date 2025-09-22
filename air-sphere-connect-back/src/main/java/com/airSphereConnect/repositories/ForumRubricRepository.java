package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.ForumRubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRubricRepository extends JpaRepository<ForumRubric, Long> {

    // Trouve une rubrique par son ID
    Optional<ForumRubric> findByIdAndDeletedAtIsNull(Long id);

    // Trouve toutes les rubriques actives
    List<ForumRubric> findAllByDeletedAtIsNull();

    // Liste toutes les rubriques actives d'un utilisateur par son ID
    List<ForumRubric> findByUserIdAndDeletedAtIsNull(Long userId);

    // Compte combien de rubriques existent dans le forum
    int countByForumIdAndDeletedAtIsNull(Long forumId);

    // Compte combien de rubriques un utilisateur a créé en tout, pour limiter
    int countByUserIdAndDeletedAtIsNull(Long userId);

    // Liste toutes les rubriques d’un forum par ordre ASC
    List<ForumRubric> findByForumIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long forumId);
    // Liste toutes les rubriques d’un forum par ordre DESC
    List<ForumRubric> findByForumIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long forumId);

    // Liste toutes les rubriques d’un utilisateur par ordre ASC
    List<ForumRubric> findByUserIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long userId);
    // Liste toutes les rubriques d’un utilisateur par ordre DESC
    List<ForumRubric> findByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);


    List<ForumRubric> findByForumIdAndDeletedAtIsNull(Long forumId);
}
