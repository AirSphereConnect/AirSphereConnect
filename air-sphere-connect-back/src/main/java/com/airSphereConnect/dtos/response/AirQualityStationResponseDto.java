package com.airSphereConnect.dtos.response;

import com.airSphereConnect.entities.AirQualityStation;

public record AirQualityStationResponseDto(
        Long id,
        String name,
        String code,
        String areaCode,
        String city
) {
}