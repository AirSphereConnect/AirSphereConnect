package com.airSphereConnect.services;

import com.airSphereConnect.dtos.response.AirQualityIndexResponseDto;
import com.airSphereConnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airSphereConnect.dtos.response.AirQualityStationResponseDto;
import com.airSphereConnect.dtos.response.AirQualityDataResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface AirQualityService {
    List<AirQualityStationResponseDto> getAllStations();

    AirQualityMeasurementResponseDto getLatestMeasurementForCity(String cityName);

    AirQualityIndexResponseDto getLatestIndexQualityForCity(String cityName);


    List<AirQualityMeasurementResponseDto> getMeasurementsHistoryForCity(
            String cityName,
            LocalDate startDate,
            LocalDate endDate
    );

    List<AirQualityIndexResponseDto> getIndicesHistoryForCity(
            String cityName,
            LocalDate startDate,
            LocalDate endDate
    );


    AirQualityDataResponseDto getCompleteDataForCity(String cityName);

    /**
     * Récupère les N plus grandes villes du département qui ont des données air quality
     * @param departmentCode Code département (2 chiffres)
     * @param limit Nombre de villes à retourner
     * @return Liste des villes avec leurs données complètes
     */
    List<AirQualityDataResponseDto> getTopCitiesWithDataInDepartment(String departmentCode, int limit);

}
