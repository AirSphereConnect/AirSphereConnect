package com.airSphereConnect.mapper.api;

import com.airSphereConnect.dtos.response.api.AirQualityDailyMeasureResponseDto;
import com.airSphereConnect.dtos.response.api.AirQualityIndexMeasureResponseDto;
import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.entities.Department;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ApiAirQualityMapper {
    public AirQualityStation toEntity(AirQualityDailyMeasureResponseDto dto) {
        if (dto == null) return null;

        AirQualityStation station = new AirQualityStation();
        station.setName(dto.nomStation());
        station.setCode(dto.codeStation());
        return station;
    }

    public AirQualityMeasurement toEntity(AirQualityDailyMeasureResponseDto dailyDto, AirQualityStation station) {
        if (dailyDto == null) return null;

        AirQualityMeasurement measurement = new AirQualityMeasurement();

        measurement.setStation(station);
        measurement.setMeasuredAt(LocalDateTime.now());
        measurement.setUnit(dailyDto.polluantUnit() != null ? dailyDto.polluantUnit() : "µg/m³");

        switch (dailyDto.polluantName().toUpperCase()) {
            case "PM10" -> measurement.setPm10(dailyDto.polluantValue());
            case "PM25" -> measurement.setPm25(dailyDto.polluantValue());
            case "NO2" -> measurement.setNo2(dailyDto.polluantValue());
            case "O3" -> measurement.setO3(dailyDto.polluantValue());
            case "SO2" -> measurement.setSo2(dailyDto.polluantValue());
            default -> {
            }
        }

        return measurement;
    }

    public AirQualityIndex toEntity(AirQualityIndexMeasureResponseDto indexDto) {
        if (indexDto == null) return null;

        AirQualityIndex index = new AirQualityIndex();
        index.setQualityIndex(Integer.valueOf(indexDto.qualityIndex()));
        index.setQualityLabel(indexDto.qualityLabel());
        index.setQualityColor(indexDto.qualityColor());
        index.setSource(indexDto.source());
        index.setAreaCode(Integer.valueOf(indexDto.areaCode()));
        index.setAreaName(indexDto.areaName());
        index.setMeasuredAt(LocalDateTime.now());

        return index;

    }
}
