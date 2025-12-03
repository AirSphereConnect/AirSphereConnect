package com.airSphereConnect.dtos;

import java.time.LocalDate;

public record ExportDto(
        LocalDate dateMesureMeteo,
        String nomVille,
        String latitude,
        String longitude,
        String population,
        String temperature,
        String humidite,
        String pression,
        String vitesseVent,
        String directionVent,
        String message,
        String stationId,
        String pm25,
        String pm10,
        String no2,
        String o3,
        String unite,
        String qualiteIndex,
        String qualiteLabel
) {
}