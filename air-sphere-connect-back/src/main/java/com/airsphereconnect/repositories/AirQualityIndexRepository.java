package com.airsphereconnect.repositories;

import com.airsphereconnect.entities.AirQualityIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour les entités AirQualityIndex.
 * Gère l'accès aux données des indices ATMO de qualité de l'air.
 * Fournit des méthodes de recherche par zone géographique avec historique temporel.
 */
@Repository
public interface AirQualityIndexRepository extends JpaRepository<AirQualityIndex, Long> {

    /**
     * Récupère le dernier indice ATMO pour une zone géographique donnée.
     * Les résultats sont triés par date de mesure décroissante.
     *
     * @param areaCode Le code de la zone géographique
     * @return Un Optional contenant l'indice le plus récent si trouvé, vide sinon
     */
    Optional<AirQualityIndex> findFirstByAreaCodeOrderByMeasuredAtDesc(String areaCode);

    /**
     * Récupère l'historique complet des indices ATMO pour une zone géographique.
     * Les résultats sont triés par date de mesure décroissante (du plus récent au plus ancien).
     *
     * @param areaCode Le code de la zone géographique
     * @return La liste de tous les indices pour cette zone, triée par date décroissante
     */
    List<AirQualityIndex> findByAreaCodeOrderByMeasuredAtDesc(String areaCode);

    /**
     * Récupère l'historique des indices ATMO pour une zone sur une période donnée.
     * Les résultats sont filtrés entre les dates de début et fin (incluses)
     * et triés par date de mesure décroissante.
     *
     * @param areaCode Le code de la zone géographique
     * @param start    La date et heure de début de la période
     * @param end      La date et heure de fin de la période
     * @return La liste des indices dans la période spécifiée, triée par date décroissante
     */
    List<AirQualityIndex> findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            String areaCode,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Recherche un indice ATMO spécifique pour une zone à une date/heure précise.
     * Utilisé pour vérifier l'existence d'un indice avant insertion (éviter les doublons).
     *
     * @param areaCode   Le code de la zone géographique
     * @param measuredAt La date et heure exacte de la mesure
     * @return Un Optional contenant l'indice si trouvé, vide sinon
     */
    Optional<AirQualityIndex> findByAreaCodeAndMeasuredAt(String areaCode, LocalDateTime measuredAt);

    List<AirQualityIndex> findByAlertTrueAndMeasuredAtBetween(LocalDateTime start, LocalDateTime end);

}
