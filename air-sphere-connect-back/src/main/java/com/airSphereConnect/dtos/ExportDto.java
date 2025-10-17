package com.airSphereConnect.dtos;

import java.time.LocalDate;

public record ExportDto(LocalDate dateMesureMeteo, String nomVille, Double latitude, Double longitude,
                        Integer population, Double temperature, Double humidite, Double pression, Double vitesseVent,
                        Double directionVent, String message, Long stationId, Double pm25, Double pm10, Double no2,
                        Double o3, String unite, Integer qualiteIndex, String qualiteLabel) {

}