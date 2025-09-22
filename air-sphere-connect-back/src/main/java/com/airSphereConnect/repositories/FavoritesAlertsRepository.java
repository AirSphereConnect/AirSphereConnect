package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.FavoritesAlerts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesAlertsRepository extends JpaRepository<FavoritesAlerts, Long> {
    List<FavoritesAlerts> findByUserId(Long userId);
    List<FavoritesAlerts> findByCityIdAndIsEnabled(Long cityId, boolean isEnabled);
}
