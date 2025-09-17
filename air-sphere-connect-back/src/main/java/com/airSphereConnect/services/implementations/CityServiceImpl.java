package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.services.CityService;

public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City getCityByName(String name) {
       return null;
    }

    @Override
    public City getCityByPostalCode(String code) {
        return null;
    }

    @Override
    public City getCityByRegion(String region) {
        return null;
    }

    @Override
    public City getCityByDepartment(String department) {
        return null;
    }
}
