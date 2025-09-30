package com.airSphereConnect.services;

import com.airSphereConnect.entities.WeatherMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WeatherService {
    WeatherMeasurement getWeatherByCityId(Long cityId);

    Page<WeatherMeasurement> findAll(Pageable pageable);

}

