package com.airSphereConnect.repositories;

import com.airSphereConnect.dtos.ExportCsvDto;
import com.airSphereConnect.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExportRepository extends JpaRepository<City, Long> {

    @Query("""
                    select new com.airSphereConnect.dtos.ExportCsvDto(
                                c.name,
                             c.areaCode,
                             aqi.areaName,
                             cast(wm.measuredAt as LocalDate),
                             avg(COALESCE(wm.temperature, 0.0)),
                             avg(COALESCE(wm.humidity, 0.0)),
                             avg(COALESCE(wm.pressure, 0.0)),
                             avg(COALESCE(wm.windSpeed, 0.0)),
                             avg(COALESCE(wm.windDirection, 0.0)),
                             max(COALESCE(wm.message, '')),
                             avg(COALESCE(aqm.pm25, 0.0)),
                             avg(COALESCE(aqm.pm10, 0.0)),
                             avg(COALESCE(aqm.no2, 0.0)),
                             avg(COALESCE(aqm.o3, 0.0)),
                             max(COALESCE(aqm.unit, '')),
                             max(COALESCE(aqi.qualityIndex, 0)),
                             max(COALESCE(aqi.qualityLabel, ''))
                         )
                         from City c
                         join c.airQualityStations aqs
                         join c.weatherMeasurements wm
                         join c.airQualityIndex aqi
                         join aqs.measurements aqm
                         where (:dateMesureMeteo is null or cast(wm.measuredAt as LocalDate) = :dateMesureMeteo)
                         and (:nomVille is null or c.name = :nomVille)
                         and (:codeZone is null or c.areaCode = :codeZone)
                         and (:nomZone is null or aqi.areaName = :nomZone)
                         and (:temperature is null or wm.temperature = :temperature)
                         and (:humidite is null or wm.humidity = :humidite)
                         and (:pression is null or wm.pressure = :pression)
                         and (:vitesseVent is null or wm.windSpeed = :vitesseVent)
                         and (:directionVent is null or wm.windDirection = :directionVent)
                         and (:message is null or wm.message = :message)
                         and (:pm25 is null or aqm.pm25 = :pm25)
                         and (:pm10 is null or aqm.pm10 = :pm10)
                         and (:no2 is null or aqm.no2 = :no2)
                         and (:o3 is null or aqm.o3 = :o3)
                         and (:unite is null or aqm.unit = :unite)
                         and (:qualiteIndex is null or aqi.qualityIndex = :qualiteIndex)
                         and (:qualiteLabel is null or aqi.qualityLabel = :qualiteLabel)
                         group by c.areaCode, aqi.areaName, cast(wm.measuredAt as LocalDate)
                         order by cast(wm.measuredAt as LocalDate) desc
            """)
    List<ExportCsvDto> findFiltered(
            @Param("nomVille") String nomVille,
            @Param("codeZone") String codeZone,
            @Param("nomZone") String nomZone,
            @Param("dateMesureMeteo") LocalDate measuredAt,
            @Param("temperature") Double temperature,
            @Param("humidite") Double humidite,
            @Param("pression") Double pression,
            @Param("vitesseVent") Double vitesseVent,
            @Param("directionVent") Double directionVent,
            @Param("message") String message,
            @Param("pm25") Double pm25,
            @Param("pm10") Double pm10,
            @Param("no2") Double no2,
            @Param("o3") Double o3,
            @Param("unite") String unite,
            @Param("qualiteIndex") Integer qualiteIndex,
            @Param("qualiteLabel") String qualiteLabel

    );

    // TODO ajouter nouvelles methodes pour filtrer différemment
}
