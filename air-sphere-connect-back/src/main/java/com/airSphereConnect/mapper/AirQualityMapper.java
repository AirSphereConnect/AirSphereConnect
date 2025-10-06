package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.AirQualityIndexResponseDto;
import com.airSphereConnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airSphereConnect.dtos.response.AirQualityStationResponseDto;
import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import org.springframework.stereotype.Component;

@Component
public class AirQualityMapper {

    /**
     * Convertit une entité AirQualityStation en DTO AirQualityStationResponseDto
     * @param station
     * @return
     */
    public AirQualityStationResponseDto toDto(AirQualityStation station) {
        if (station == null) return null;

        return new AirQualityStationResponseDto(
                station.getId(),
                station.getName(),
                station.getCode(),
                station.getAreaCode(),
                station.getCity() != null ? station.getCity().getName() : null
        );
    }


    public AirQualityMeasurementResponseDto toDto(AirQualityMeasurement measurement) {
        if (measurement == null) return null;

        return new AirQualityMeasurementResponseDto(
                measurement.getId(),
                measurement.getPm10(),
                measurement.getPm25(),
                measurement.getNo2(),
                measurement.getO3(),
                measurement.getSo2(),
                measurement.getUnit(),
                measurement.getMeasuredAt(),
                measurement.getStation() != null ? measurement.getStation().getName() : null
        );
    }


    public AirQualityIndexResponseDto toDto(AirQualityIndex index, String alertMessage) {
        if (index == null) return null;

        return new AirQualityIndexResponseDto(
                index.getId(),
                index.getQualityIndex(),
                index.getQualityLabel(),
                index.getQualityColor(),
                index.getMeasuredAt(),
                index.getAreaCode(),
                index.getAreaName(),
                index.getSource(),
                alertMessage
        );
    }


}
