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

@Repository
public interface AirQualityMeasurementRepository extends JpaRepository<AirQualityMeasurement, Long> {

    @Query("SELECT m FROM AirQualityMeasurement m " +
            "WHERE m.station.city.areaCode = :areaCode " +
            "ORDER BY m.measuredAt DESC " +
            "LIMIT 1")
    Optional<AirQualityMeasurement> findTopByAreaCodeOrderByMeasuredAtDesc(@Param("areaCode") String areaCode);

    @Query("SELECT m FROM AirQualityMeasurement m " +
            "WHERE m.station.city.areaCode = :areaCode " +
            "ORDER BY m.measuredAt DESC")
    List<AirQualityMeasurement> findByAreaCodeOrderByMeasuredAtDesc(@Param("areaCode") String areaCode);


    @Query("SELECT m FROM AirQualityMeasurement m " +
            "WHERE m.station.city.areaCode = :areaCode " +
            "AND m.measuredAt BETWEEN :start AND :end " +
            "ORDER BY m.measuredAt DESC")
    List<AirQualityMeasurement> findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            @Param("areaCode") String areaCode,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    boolean existsByStationAndMeasuredAt(AirQualityStation station, LocalDateTime measuredAt);

    @Query("SELECT m FROM AirQualityMeasurement m " +
            "WHERE m.measuredAt > :since " +
            "ORDER BY m.station.id ASC, m.measuredAt DESC")
    List<AirQualityMeasurement> findByMeasuredAtAfterOrderByStationIdAscMeasuredAtDesc(@Param("since") LocalDateTime since);

    List<AirQualityMeasurement> findByStationCityPostalCodeAndMeasuredAtAfterOrderByMeasuredAtDesc(
            String postalCode,
            LocalDateTime since
    );
}