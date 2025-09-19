package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.CityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.mapper.CityMapper;
import com.airSphereConnect.services.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public List<CityResponseDto> getAllCities() {
        return cityService.getAllCities().stream().map(CityMapper::toDto).toList();
    }

    @GetMapping("/postal-code/{postalCode}")
    public CityResponseDto getCityByPostalCode(@PathVariable String postalCode) {
        City city = cityService.getCityByPostalCode(postalCode);
        return CityMapper.toDto(city);
    }

    @GetMapping("/city/{name}")
    public CityResponseDto getCityByName(@PathVariable String name) {
        City city = cityService.getCityByName(name);
        return CityMapper.toDto(city);
    }

    @GetMapping("/region/{region}")
    public List<CityResponseDto> getCitiesByRegion(@PathVariable String region) {
        List<City> cities = cityService.getCitiesByRegionName(region);
        return cities.stream().map(CityMapper::toDto).toList();
    }

    @GetMapping("/departmentName/{departmentName}")
    public List<CityResponseDto> getCitiesByDepartmentName(@PathVariable String departmentName) {
        List<City> cities = cityService.getCitiesByDepartmentName(departmentName);
        return cities.stream().map(CityMapper::toDto).toList();
    }

    @GetMapping("/departmentCode/{departmentCode}")
    public List<CityResponseDto> getCitiesByDepartmentCode(@PathVariable String departmentCode) {
        List<City> cities = cityService.getCitiesByDepartmentCode(departmentCode);
        return cities.stream().map(CityMapper::toDto).toList();
    }
}
