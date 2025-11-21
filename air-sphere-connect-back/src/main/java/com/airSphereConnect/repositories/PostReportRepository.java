package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {

    // Trouver les signalements d'un post (non supprimés)
    List<PostReport> findByPostIdAndDeletedAtIsNull(Long postId);

    // Trouver les signalements d'un utilisateur (non supprimés)
    List<PostReport> findByUserIdAndDeletedAtIsNull(Long userId);

    // Trouver les signalements par statut (non supprimés)
    List<PostReport> findByStatusAndDeletedAtIsNull(String status);

    // Trouver un signalement spécifique (post + utilisateur, non supprimé)
    Optional<PostReport> findByPostIdAndUserIdAndDeletedAtIsNull(Long postId, Long userId);

    // Trouver un signalement par ID (non supprimé)
    Optional<PostReport> findByIdAndDeletedAtIsNull(Long id);
}

