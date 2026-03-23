package com.airsphereconnect.services;

import com.airsphereconnect.dtos.response.AirQualityIndexResponseDto;
import com.airsphereconnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airsphereconnect.dtos.response.AirQualityStationResponseDto;
import com.airsphereconnect.dtos.response.AirQualityDataResponseDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Service gérant les opérations liées à la qualité de l'air.
 * Fournit des méthodes pour récupérer les stations, mesures, indices et historiques de qualité de l'air.
 */
public interface AirQualityService {
    /**
     * Récupère toutes les stations de mesure de la qualité de l'air.
     *
     * @return La liste de toutes les stations
     */
    List<AirQualityStationResponseDto> getAllStations();

    /**
     * Récupère la dernière mesure de qualité de l'air pour une ville donnée.
     *
     * @param cityName Le nom de la ville
     * @return La dernière mesure de qualité de l'air
     */
    AirQualityMeasurementResponseDto getLatestMeasurementForCity(String cityName);

    /**
     * Récupère le dernier indice de qualité de l'air pour une ville donnée.
     *
     * @param cityName Le nom de la ville
     * @return Le dernier indice de qualité de l'air
     */
    AirQualityIndexResponseDto getLatestIndexQualityForCity(String cityName);

    /**
     * Récupère l'historique des mesures de qualité de l'air pour une ville sur une période donnée.
     *
     * @param cityName  Le nom de la ville
     * @param startDate La date de début de la période
     * @param endDate   La date de fin de la période
     * @return La liste des mesures sur la période
     */
    List<AirQualityMeasurementResponseDto> getMeasurementsHistoryForCity(
            String cityName,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Récupère l'historique des indices de qualité de l'air pour une ville sur une période donnée.
     *
     * @param cityName  Le nom de la ville
     * @param startDate La date de début de la période
     * @param endDate   La date de fin de la période
     * @return La liste des indices sur la période
     */
    List<AirQualityIndexResponseDto> getIndicesHistoryForCity(
            String cityName,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Récupère toutes les données de qualité de l'air pour une ville (mesures et indices).
     *
     * @param cityName Le nom de la ville
     * @return Les données complètes de qualité de l'air
     */
    AirQualityDataResponseDto getCompleteDataForCity(String cityName);

    /**
     * Récupère les N plus grandes villes du département qui ont des données de qualité de l'air.
     *
     * @param departmentCode Le code du département (2 chiffres)
     * @param limit          Le nombre de villes à retourner
     * @return La liste des villes avec leurs données complètes
     */
    List<AirQualityDataResponseDto> getTopCitiesWithDataInDepartment(String departmentCode, int limit);

}
