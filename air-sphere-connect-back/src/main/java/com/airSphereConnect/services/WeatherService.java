package com.airSphereConnect.services;

import com.airSphereConnect.entities.WeatherMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WeatherService {
    WeatherMeasurement getWeatherByCityId(Long cityId);

    Page<WeatherMeasurement> findAll(Pageable pageable);

    List<WeatherMeasurement> getWeatherHistoryByCityId(Long cityId);

}

