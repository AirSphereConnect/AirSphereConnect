package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AirQualityIndexRepository extends JpaRepository<AirQualityIndex, Long> {

    Optional<AirQualityIndex> findFirstByAreaCodeOrderByMeasuredAtDesc(String areaCode);

    @Query("SELECT i FROM AirQualityIndex i " +
            "WHERE i.areaCode = :areaCode " +
            "AND i.measuredAt BETWEEN :start AND :end " +
            "ORDER BY i.measuredAt DESC")
    List<AirQualityIndex> findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            @Param("areaCode") Integer areaCode,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
