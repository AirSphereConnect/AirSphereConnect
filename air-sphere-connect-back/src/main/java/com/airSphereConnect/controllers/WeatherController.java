package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.WeatherResponseDto;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.airSphereConnect.mapper.WeatherMapper;
import com.airSphereConnect.services.WeatherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final WeatherMapper weatherMapper;

    public WeatherController(WeatherService weatherService, WeatherMapper weatherMapper) {
        this.weatherService = weatherService;
        this.weatherMapper = weatherMapper;
    }

    @GetMapping("/latest")
    public List<WeatherResponseDto> getAllWeather(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("measuredAt").descending());
        Page<WeatherMeasurement> pageResult = weatherService.findAll(pageable); // Tu peux créer cette méthode si besoin
        return pageResult.stream()
                .map(weatherMapper::toDto)
                .toList();
    }

    @GetMapping("/city/{id}")
    public WeatherResponseDto getWeatherCityById(@PathVariable Long id) {
        WeatherMeasurement weather = weatherService.getWeatherByCityId(id);

        return weatherMapper.toDto(weather);
    }

    @GetMapping("/city/history/{id}")
    public List<WeatherResponseDto> getWeatherHistoryByCityId(@PathVariable Long id) {
        List<WeatherMeasurement> measurements = weatherService.getWeatherHistoryByCityId(id);
        return measurements.stream()
                .map(weatherMapper::toDto)
                .collect(Collectors.toList());
    }


}


