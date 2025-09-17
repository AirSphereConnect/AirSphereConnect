package com.airSphereConnect.services;

import com.airSphereConnect.entities.City;

public interface CityService {

    City getCityByName(String code);

    City getCityByPostalCode(String code);

    City getCityByRegion(String region);

    City getCityByDepartment(String department);

}