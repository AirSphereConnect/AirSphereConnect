package com.airSphereConnect.repositories;

import com.airSphereConnect.dtos.ExportDto;
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
