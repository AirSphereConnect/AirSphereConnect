package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour les entités AirQualityMeasurement.
 * Gère l'accès aux données des mesures de polluants atmosphériques (PM10, PM2.5, NO2, O3, SO2).
 * Implémente une stratégie de recherche multi-niveaux : INSEE (ville) -> areaCode (intercommunalité) -> département.
 */
@Repository
public interface AirQualityMeasurementRepository extends JpaRepository<AirQualityMeasurement, Long> {

    /**
     * Récupère la dernière mesure pour une ville identifiée par son code INSEE.
     * Utilisé comme premier niveau de la stratégie de fallback (recherche directe par ville).
     *
     * @param inseeCode Le code INSEE de la ville
     * @return Un Optional contenant la mesure la plus récente si trouvée, vide sinon
     */
    Optional<AirQualityMeasurement> findTopByStation_City_InseeCodeOrderByMeasuredAtDesc(String inseeCode);

    /**
     * Récupère l'historique complet des mesures pour une ville identifiée par son code INSEE.
     * Les résultats sont triés par date de mesure décroissante.
     *
     * @param inseeCode Le code INSEE de la ville
     * @return La liste de toutes les mesures pour cette ville, triée par date décroissante
     */
    List<AirQualityMeasurement> findByStation_City_InseeCodeOrderByMeasuredAtDesc(String inseeCode);

    /**
     * Récupère l'historique des mesures pour une ville sur une période donnée.
     * Filtre les mesures par code INSEE et période temporelle.
     *
     * @param inseeCode Le code INSEE de la ville
     * @param start     La date et heure de début de la période
     * @param end       La date et heure de fin de la période
     * @return La liste des mesures dans la période spécifiée, triée par date décroissante
     */
    List<AirQualityMeasurement> findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            String inseeCode, LocalDateTime start, LocalDateTime end);

    /**
     * Récupère la dernière mesure pour une zone géographique (intercommunalité) identifiée par son areaCode.
     * Utilisé comme deuxième niveau de la stratégie de fallback si aucune mesure directe n'est disponible pour la ville.
     *
     * @param areaCode Le code de la zone géographique (intercommunalité)
     * @return Un Optional contenant la mesure la plus récente de la zone si trouvée, vide sinon
     */
    Optional<AirQualityMeasurement> findTopByStation_City_AreaCodeOrderByMeasuredAtDesc(String areaCode);

    /**
     * Récupère l'historique complet des mesures pour une zone géographique (intercommunalité).
     * Les résultats sont triés par date de mesure décroissante.
     *
     * @param areaCode Le code de la zone géographique
     * @return La liste de toutes les mesures pour cette zone, triée par date décroissante
     */
    List<AirQualityMeasurement> findByStation_City_AreaCodeOrderByMeasuredAtDesc(String areaCode);

    /**
     * Récupère l'historique des mesures pour une zone géographique sur une période donnée.
     * Filtre les mesures par areaCode et période temporelle.
     *
     * @param areaCode Le code de la zone géographique
     * @param start    La date et heure de début de la période
     * @param end      La date et heure de fin de la période
     * @return La liste des mesures dans la période spécifiée, triée par date décroissante
     */
    List<AirQualityMeasurement> findByStation_City_AreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            String areaCode, LocalDateTime start, LocalDateTime end);

    /**
     * Récupère les dernières mesures distinctes pour chaque station d'un département.
     * Utilisé comme troisième niveau de la stratégie de fallback (recherche au niveau départemental).
     * Extrait le code département (2 premiers chiffres du code INSEE) et retourne
     * uniquement la mesure la plus récente de chaque station.
     *
     * @param departmentCode Le code du département (2 chiffres, ex: "31" pour Haute-Garonne)
     * @return La liste des mesures les plus récentes par station dans le département
     */
    @Query("SELECT DISTINCT m FROM AirQualityMeasurement m WHERE SUBSTRING(m.station.city.inseeCode, 1, 2) = :departmentCode AND m.measuredAt = (SELECT MAX(m2.measuredAt) FROM AirQualityMeasurement m2 WHERE m2.station = m.station)")
    List<AirQualityMeasurement> findLatestByDepartmentCode(@Param("departmentCode") String departmentCode);

    /**
     * Récupère l'historique complet des mesures pour un département.
     * Extrait le code département des 2 premiers chiffres du code INSEE.
     * Les résultats sont triés par date de mesure décroissante.
     *
     * @param departmentCode Le code du département (2 chiffres)
     * @return La liste de toutes les mesures du département, triée par date décroissante
     */
    @Query("SELECT m FROM AirQualityMeasurement m WHERE SUBSTRING(m.station.city.inseeCode, 1, 2) = :departmentCode ORDER BY m.measuredAt DESC")
    List<AirQualityMeasurement> findByDepartmentCodeOrderByMeasuredAtDesc(@Param("departmentCode") String departmentCode);

    /**
     * Récupère l'historique des mesures pour un département sur une période donnée.
     * Filtre les mesures par département (2 premiers chiffres du code INSEE) et période temporelle.
     *
     * @param departmentCode Le code du département (2 chiffres)
     * @param start          La date et heure de début de la période
     * @param end            La date et heure de fin de la période
     * @return La liste des mesures dans la période spécifiée, triée par date décroissante
     */
    @Query("SELECT m FROM AirQualityMeasurement m WHERE SUBSTRING(m.station.city.inseeCode, 1, 2) = :departmentCode AND m.measuredAt BETWEEN :start AND :end ORDER BY m.measuredAt DESC")
    List<AirQualityMeasurement> findByDepartmentCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            @Param("departmentCode") String departmentCode,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Vérifie l'existence d'une mesure pour une station à une date/heure précise.
     * Utilisé pour éviter les doublons lors de la synchronisation des données depuis l'API externe.
     *
     * @param station    La station de mesure
     * @param measuredAt La date et heure de la mesure
     * @return true si une mesure existe déjà, false sinon
     */
    boolean existsByStationAndMeasuredAt(AirQualityStation station, LocalDateTime measuredAt);

    /**
     * Récupère toutes les mesures effectuées après une date donnée.
     * Les résultats sont triés par identifiant de station (ascendant) puis par date de mesure (descendant).
     * Utilisé pour les fonctionnalités d'export et de synchronisation incrémentale.
     *
     * @param since La date/heure de référence
     * @return La liste des mesures postérieures à la date spécifiée
     */
    List<AirQualityMeasurement> findByMeasuredAtAfterOrderByStation_IdAscMeasuredAtDesc(LocalDateTime since);

}
