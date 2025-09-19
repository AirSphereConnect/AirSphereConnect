package com.airSphereConnect.services;

import com.airSphereConnect.entities.City;

import java.util.List;

public interface CityService {

    List<City> getAllCities();

    City getCityByName(String name);

    City getCityByPostalCode(String code);

    List<City> getCitiesByRegionName(String region);

    List<City> getCitiesByDepartmentName(String departmentName);

    List<City> getCitiesByDepartmentCode(String departmentCode);

}