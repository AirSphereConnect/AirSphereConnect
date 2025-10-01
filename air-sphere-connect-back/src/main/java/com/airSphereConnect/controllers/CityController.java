package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.CityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.mapper.CityMapper;
import com.airSphereConnect.services.CityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;
    private final CityMapper cityMapper;

    public CityController(CityService cityService, CityMapper cityMapper) {
        this.cityService = cityService;
        this.cityMapper = cityMapper;
    }

    @GetMapping
    public List<CityResponseDto> getAllCities() {
        return cityService.getAllCities().stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/insee-code/{inseeCode}")
    public CityResponseDto getCityByInseeCode(@PathVariable String inseeCode) {
        City city = cityService.getCityByInseeCode(inseeCode);
        return cityMapper.toDto(city);
    }

    @GetMapping("/postal-code/{postalCode}")
    public CityResponseDto getCityByPostalCode(@PathVariable String postalCode) {
        City city = cityService.getCitiesByPostalCode(postalCode);
        return cityMapper.toDto(city);
    }

    @GetMapping("/city")
    public CityResponseDto getCityByName(@RequestParam String name) {
        City city = cityService.getCityByName(name);
        return cityMapper.toDto(city);
    }

    @GetMapping("/region/{region}")
    public List<CityResponseDto> getCitiesByRegion(@PathVariable String region) {
        return cityService.getCitiesByRegionName(region).stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/departmentName/{departmentName}")
    public List<CityResponseDto> getCitiesByDepartmentName(@PathVariable String departmentName) {
        return cityService.getCitiesByDepartmentName(departmentName).stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/departmentCode/{departmentCode}")
    public List<CityResponseDto> getCitiesByDepartmentCode(@PathVariable String departmentCode) {
        return cityService.getCitiesByDepartmentCode(departmentCode).stream().map(cityMapper::toDto).toList();
    }

    @GetMapping("/search")
    public List<CityResponseDto> searchCitiesByPopulation(
            @RequestParam(required = false) Integer populationMin,
            @RequestParam(required = false) Integer populationMax) {

        if (populationMin != null && populationMax != null) {
            return cityService.getCitiesByPopulationBetweenThan(populationMin, populationMax).stream().map(cityMapper::toDto).toList();
        } else if (populationMin != null) {
            return cityService.getCitiesByPopulationGreaterThanEqual(populationMin).stream().map(cityMapper::toDto).toList();
        } else if (populationMax != null) {
            return cityService.getCitiesByPopulationLessThanEqual(populationMax).stream().map(cityMapper::toDto).toList();
        }
        return cityService.getAllCities().stream().map(cityMapper::toDto).toList();
    }
}
