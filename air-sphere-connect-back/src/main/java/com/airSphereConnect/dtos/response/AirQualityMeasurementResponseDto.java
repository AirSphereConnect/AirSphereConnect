package com.airSphereConnect.dtos.response;

import java.time.LocalDateTime;

public record AirQualityMeasurementResponseDto(
        Long id, Double pm10,
        Double pm25,
        Double no2,
        Double o3,
        Double so2,
        String unit,
        LocalDateTime measuredAt,
        String station

) {}
