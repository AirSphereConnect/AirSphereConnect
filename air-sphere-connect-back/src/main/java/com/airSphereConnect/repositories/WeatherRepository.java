package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.WeatherMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository <WeatherMeasurement, Long> {
    Optional<WeatherMeasurement> findTopByCityIdOrderByMeasuredAtDesc(Long cityId);
}
