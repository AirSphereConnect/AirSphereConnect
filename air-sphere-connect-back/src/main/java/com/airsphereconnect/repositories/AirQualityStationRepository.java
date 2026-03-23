package com.airsphereconnect.repositories;

import com.airsphereconnect.dtos.ExportDto;
import com.airsphereconnect.entities.AirQualityStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour les entités AirQualityStation.
 * Gère l'accès aux données des stations de mesure de la qualité de l'air.
 * Fournit des méthodes de recherche par code, zone géographique et export de données.
 */
@Repository
public interface AirQualityStationRepository extends JpaRepository<AirQualityStation, Long> {

    /**
     * Recherche une station de mesure par son code unique.
     *
     * @param code Le code unique de la station
     * @return Un Optional contenant la station si trouvée, vide sinon
     */
    Optional<AirQualityStation> findByCode(String code);

    /**
     * Recherche toutes les stations situées dans une zone géographique donnée.
     * Utilise le code de zone (areaCode) de la ville associée à la station.
     *
     * @param areaCode Le code de la zone géographique (ex: code intercommunalité)
     * @return La liste des stations dans cette zone
     */
    @Query("SELECT s FROM AirQualityStation s WHERE s.city.areaCode = :areaCode")
    List<AirQualityStation> findByAreaCode(@Param("areaCode") String areaCode);


    /**
     * Récupère les données d'export pour une station spécifique.
     * Agrège les informations de la station, ses mesures de polluants (PM10, PM2.5, NO2, O3),
     * et l'indice de qualité de l'air de la ville associée.
     * Utilisé pour les fonctionnalités d'export de données.
     *
     * @param stationId L'identifiant unique de la station
     * @return La liste des DTOs d'export contenant les données agrégées
     */
    @Query("""
        select
        aqs.name,
        aqm.pm10,
        aqm.pm25,
        aqm.no2,
        aqm.o3,
        aqi.qualityIndex,
        aqi.qualityLabel
        from AirQualityStation aqs
        join aqs.measurements aqm
        join aqs.city c
        join c.airQualityIndex aqi
        where aqs.id = :stationId
""")
    List<ExportDto> findByStationId(@Param("stationId") Long stationId);
}
