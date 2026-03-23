package com.airsphereconnect.services;

import com.airsphereconnect.entities.WeatherMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WeatherService {
    WeatherMeasurement getWeatherByCityId(Long cityId);

    Page<WeatherMeasurement> findAll(Pageable pageable);

    List<WeatherMeasurement> getWeatherHistoryByCityId(Long cityId);

}

