package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.WeatherAlertDto;
import com.airSphereConnect.dtos.response.WeatherDescriptionDto;
import com.airSphereConnect.dtos.response.WeatherResponseDto;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class WeatherMapper {

    private final ObjectMapper objectMapper;

    public WeatherMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WeatherResponseDto toDto(WeatherMeasurement weather) {
        if (weather == null) return null;

        WeatherDescriptionDto[] messageDto = null;
        WeatherAlertDto[]  alertMessageDto = null;
        try {
            if (weather.getMessage() != null) {
                messageDto = objectMapper.readValue(weather.getMessage(), WeatherDescriptionDto[].class);
            }
            if (weather.getAlert() == true) {
                alertMessageDto = objectMapper.readValue(weather.getMessage(), WeatherAlertDto[].class);
            }

        } catch (JsonProcessingException e) {
            messageDto = new WeatherDescriptionDto[0];
            alertMessageDto = new WeatherAlertDto[0];
        }

        return new WeatherResponseDto(
                weather.getCity().getId(),
                weather.getCity().getName(),
                weather.getMeasuredAt(),
                weather.getTemperature(),
                weather.getHumidity(),
                weather.getPressure(),
                weather.getWindSpeed(),
                weather.getWindDirection(),
                messageDto,
                weather.getAlert(),
                alertMessageDto
        );
    }
}

