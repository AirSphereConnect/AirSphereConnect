package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.AirQualityIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AirQualityIndexRepository extends JpaRepository<AirQualityIndex, Long> {

    // Récupère le dernier indice pour une zone
    Optional<AirQualityIndex> findFirstByAreaCodeOrderByMeasuredAtDesc(String areaCode);

    // Récupère tout l'historique pour une zone par date décroissante
    List<AirQualityIndex> findByAreaCodeOrderByMeasuredAtDesc(String areaCode);

    // Récupère l'historique pour une zone dans une plage de dates
    List<AirQualityIndex> findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            String areaCode,
            LocalDateTime start,
            LocalDateTime end
    );

}
