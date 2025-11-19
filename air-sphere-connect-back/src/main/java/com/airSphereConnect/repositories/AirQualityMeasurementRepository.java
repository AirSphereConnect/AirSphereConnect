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

    // Recherche par département (2 premiers chiffres du code INSEE)
    // Retourne toutes les mesures récentes du département pour pouvoir trouver la ville la plus proche
    @Query("SELECT DISTINCT m FROM AirQualityMeasurement m WHERE SUBSTRING(m.station.city.inseeCode, 1, 2) = :departmentCode AND m.measuredAt = (SELECT MAX(m2.measuredAt) FROM AirQualityMeasurement m2 WHERE m2.station = m.station)")
    List<AirQualityMeasurement> findLatestByDepartmentCode(@Param("departmentCode") String departmentCode);

    @Query("SELECT m FROM AirQualityMeasurement m WHERE SUBSTRING(m.station.city.inseeCode, 1, 2) = :departmentCode ORDER BY m.measuredAt DESC")
    List<AirQualityMeasurement> findByDepartmentCodeOrderByMeasuredAtDesc(@Param("departmentCode") String departmentCode);

    @Query("SELECT m FROM AirQualityMeasurement m WHERE SUBSTRING(m.station.city.inseeCode, 1, 2) = :departmentCode AND m.measuredAt BETWEEN :start AND :end ORDER BY m.measuredAt DESC")
    List<AirQualityMeasurement> findByDepartmentCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            @Param("departmentCode") String departmentCode,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    boolean existsByStationAndMeasuredAt(AirQualityStation station, LocalDateTime measuredAt);

    List<AirQualityMeasurement> findByMeasuredAtAfterOrderByStation_IdAscMeasuredAtDesc(LocalDateTime since);
}
