package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.WeatherResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.WeatherMeasurement;
import org.springframework.stereotype.Component;

@Component
public class WeatherMapper {
    // Backend -> Frontend
    public WeatherResponseDto toDto(WeatherMeasurement weather) {
        if (weather == null) return null;

        return new WeatherResponseDto(
                weather.getCity().getId(),
                weather.getCity().getName(),
                weather.getTemperature(),
                weather.getHumidity(),
                weather.getWindSpeed(),
                weather.getWindDirection(),
                weather.getPressure(),
                weather.getMessage(),
                weather.getAlert(),
                weather.getAlertMessage()
        );
    }
}

