package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.AirQualityStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirQualityStationRepository extends JpaRepository<AirQualityStation, Long> {

    Optional<AirQualityStation> findByCode(String code);

    @Query("SELECT s FROM AirQualityStation s WHERE s.city.areaCode = :areaCode")
    List<AirQualityStation> findByAreaCode(@Param("areaCode") String areaCode);
}
