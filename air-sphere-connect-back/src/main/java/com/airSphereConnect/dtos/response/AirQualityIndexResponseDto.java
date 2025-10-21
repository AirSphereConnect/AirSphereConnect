package com.airSphereConnect.dtos.response;

import java.time.LocalDateTime;

public record AirQualityIndexResponseDto(
        Long id,
        Integer qualityIndex,
        String qualityLabel,
        String qualityColor,
        LocalDateTime measuredAt,
        String areaCode,
        String areaName,
        String source,
        String alertMessage
) {

}
