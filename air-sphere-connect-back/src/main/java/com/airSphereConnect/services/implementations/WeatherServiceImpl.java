package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.WeatherMeasurement;
import com.airSphereConnect.repositories.WeatherRepository;
import com.airSphereConnect.services.WeatherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final WeatherRepository weatherRepository;

    public WeatherServiceImpl(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @Override
    public WeatherMeasurement getWeatherByCityId(Long cityId) {
        Optional<WeatherMeasurement> optionalWeather = weatherRepository
                .findTopByCityIdOrderByMeasuredAtDesc(cityId);
        return optionalWeather.orElse(null);
    }

    @Override
    public List<WeatherMeasurement> getWeatherHistoryByCityId(Long cityId) {
        return weatherRepository.findByCityId(cityId);
    }




    @Override
    public Page<WeatherMeasurement> findAll(Pageable pageable) {
        return weatherRepository.findAll(pageable);
    }

}

