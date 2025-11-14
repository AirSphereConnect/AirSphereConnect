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

    Optional<AirQualityMeasurement> findTopByStation_City_InseeCodeOrderByMeasuredAtDesc(String inseeCode);

    List<AirQualityMeasurement> findByStation_City_InseeCodeOrderByMeasuredAtDesc(String inseeCode);

    List<AirQualityMeasurement> findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            String inseeCode, LocalDateTime start, LocalDateTime end);

    Optional<AirQualityMeasurement> findTopByStation_City_AreaCodeOrderByMeasuredAtDesc(String areaCode);

    List<AirQualityMeasurement> findByStation_City_AreaCodeOrderByMeasuredAtDesc(String areaCode);

    List<AirQualityMeasurement> findByStation_City_AreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            String areaCode, LocalDateTime start, LocalDateTime end);

    boolean existsByStationAndMeasuredAt(AirQualityStation station, LocalDateTime measuredAt);

    List<AirQualityMeasurement> findByMeasuredAtAfterOrderByStation_IdAscMeasuredAtDesc(LocalDateTime since);

}
