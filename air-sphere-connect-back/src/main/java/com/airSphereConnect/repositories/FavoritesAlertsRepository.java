package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.FavoritesAlerts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesAlertsRepository extends JpaRepository<FavoritesAlerts, Long> {
    Optional<FavoritesAlerts> findByIdAndUserId(Long id, Long userId);
    List<FavoritesAlerts> findByCityIdAndIsEnabled(Long cityId, boolean isEnabled);
}
