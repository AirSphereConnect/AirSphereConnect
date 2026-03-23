package com.airsphereconnect.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Mesure détaillée des polluants atmosphériques")
public record AirQualityMeasurementResponseDto(
        @Schema(description = "Identifiant unique de la mesure", example = "1")
        Long id,
        @Schema(description = "Concentration PM10 en µg/m³", example = "15.5")
        Double pm10,
        @Schema(description = "Concentration PM2.5 en µg/m³", example = "8.3")
        Double pm25,
        @Schema(description = "Concentration NO2 en µg/m³", example = "25.7")
        Double no2,
        @Schema(description = "Concentration O3 en µg/m³", example = "45.2")
        Double o3,
        @Schema(description = "Concentration SO2 en µg/m³", example = "5.1")
        Double so2,
        @Schema(description = "Unité de mesure", example = "µg/m³")
        String unit,
        @Schema(description = "Date et heure de la mesure")
        LocalDateTime measuredAt,
        @Schema(description = "Nom de la station de mesure", example = "Montpellier - Chaptal Urbain")
        String station,
        @Schema(description = "Source des données (exact, areaCode, department)", example = "exact")
        String dataSource, // "exact", "areaCode", ou "department"
        @Schema(description = "Villes sources pour fallback département", example = "Montpellier, Nîmes")
        String sourceCities // Noms des villes sources (pour fallback département)
) {}
