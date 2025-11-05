package com.airSphereConnect.services;

import com.airSphereConnect.entities.City;

import java.util.Collection;
import java.util.List;

public interface CityService {

    List<City> getAllCities();

    City getCityByName(String name);

    City getCityByInseeCode(String inseeCode);

    City getCitiesByPostalCode(String code);

    List<City> getCitiesByRegionName(String region);

    List<City> getCitiesByDepartmentName(String departmentName);

    List<City> getCitiesByDepartmentCode(String departmentCode);

    List<City> getCitiesByPopulationGreaterThanEqual(Integer population);

    List<City> getCitiesByPopulationLessThanEqual(Integer population);

    List<City> getCitiesByPopulationBetweenThan(Integer populationMin, Integer populationMax);

    List<City> findByNameContainingIgnoreCase(String query);

    List<City> getTopCitiesByAreaCode(String areaCode, int limit);
}