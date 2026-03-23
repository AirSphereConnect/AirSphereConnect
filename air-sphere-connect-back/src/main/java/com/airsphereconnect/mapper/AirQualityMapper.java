package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.response.AirQualityIndexResponseDto;
import com.airsphereconnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airsphereconnect.dtos.response.AirQualityStationResponseDto;
import com.airsphereconnect.entities.AirQualityIndex;
import com.airsphereconnect.entities.AirQualityMeasurement;
import com.airsphereconnect.entities.AirQualityStation;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir les entités de qualité de l'air en DTOs de réponse.
 * Gère la transformation des entités JPA (AirQualityStation, AirQualityMeasurement, AirQualityIndex)
 * vers leurs représentations DTO pour l'API REST.
 */
@Component
public class AirQualityMapper {

    /**
     * Convertit une entité AirQualityStation en DTO de réponse.
     * Extrait les informations de la station incluant le nom de la ville associée.
     *
     * @param station L'entité station à convertir
     * @return Le DTO de réponse correspondant, ou null si l'entité est null
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


    /**
     * Convertit une entité AirQualityMeasurement en DTO de réponse avec source de données par défaut.
     * Utilise "exact" comme source de données et null pour les villes sources (données directes de la ville).
     * @param measurement L'entité mesure à convertir
     * @return Le DTO de réponse avec les concentrations de polluants, ou null si l'entité est null
     */
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
                measurement.getStation() != null ? measurement.getStation().getName() : null,
                "exact", // Par défaut, données exactes
                null // Pas de villes sources pour données exactes
        );
    }

    /**
     * Convertit une entité AirQualityMeasurement en DTO de réponse avec source de données personnalisée.
     * Utilisé lorsque les données proviennent d'une stratégie de fallback (intercommunalité ou département).
     * Permet d'indiquer explicitement la source des données et les villes sources utilisées.
     * @param measurement  L'entité mesure à convertir
     * @param dataSource   La source des données (ex: "intercommunalité", "département")
     * @param sourceCities Les noms des villes sources utilisées pour la mesure
     * @return Le DTO de réponse avec métadonnées sur l'origine des données, ou null si l'entité est null
     */
    public AirQualityMeasurementResponseDto toDto(AirQualityMeasurement measurement, String dataSource, String sourceCities) {
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
                measurement.getStation() != null ? measurement.getStation().getName() : null,
                dataSource,
                sourceCities
        );
    }


    /**
     * Convertit une entité AirQualityIndex en DTO de réponse avec un message d'alerte personnalisé.
     * L'indice ATMO varie de 1 (très bon) à 6 (extrêmement mauvais).
     * Le message d'alerte est calculé en fonction du niveau de l'indice et de l'état d'alerte.
     *
     * @param index        L'entité indice de qualité de l'air à convertir
     * @param alertMessage Le message d'alerte personnalisé à inclure dans le DTO
     * @return Le DTO de réponse avec l'indice ATMO et le message d'alerte, ou null si l'entité est null
     */
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
