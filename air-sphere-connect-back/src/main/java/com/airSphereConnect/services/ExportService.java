package com.airSphereConnect.services;

import com.airSphereConnect.dtos.ExportCsvDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ExportService {

    // Récupère les données filtrées en fonction des critères fournis
    List<ExportCsvDto> getFilteredData(
            String nomVille,
            String areaCode,
            String areaName,
            LocalDate dateMesureMeteo,
            Double temperature,
            Double humidite,
            Double pression,
            Double vitesseVent,
            Double directionVent,
            String message,
            Double pm25,
            Double pm10,
            Double no2,
            Double o3,
            String unite,
            Integer qualiteIndex,
            String qualiteLabel
    );

    // Exporte les données au format CSV
    byte[] exportToCsv(
            String nomVille,
            String areaCode,
            String areaName,
            LocalDate dateMesureMeteo,
            Double temperature,
            Double humidite,
            Double pression,
            Double vitesseVent,
            Double directionVent,
            String message,
            Double pm25,
            Double pm10,
            Double no2,
            Double o3,
            String unite,
            Integer qualiteIndex,
            String qualiteLabel
    );
}
