package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.services.CityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public City getCityByName(String name) {
        return cityRepository.getCityByNameIgnoreCase(name).orElseThrow(() -> new GlobalException.RessourceNotFoundException(
                "City with name " + name + " not found."));
    }

    @Override
    public City getCityByPostalCode(String code) {
        return cityRepository.getCityByPostalCode(String.valueOf(code)).orElseThrow(() -> new GlobalException.RessourceNotFoundException(
                "City with name " + code + " not found."));
    }

    @Override
    public List<City> getCitiesByRegionName(String regionName) {
        List<City> cities = cityRepository.getCitiesByDepartmentRegionNameIgnoreCase(regionName);

        if (cities.isEmpty()) {
            throw new GlobalException.RessourceNotFoundException(
                    "No cities found in this region: " + regionName);
        }
        return cities;
    }

    @Override
    public List<City> getCitiesByDepartmentName(String departmentName) {
        List<City> cities = cityRepository.getCitiesByDepartmentNameIgnoreCase(departmentName);

        if (cities.isEmpty()) {
            throw new GlobalException.RessourceNotFoundException(
                    "No cities found in this region: " + departmentName);
        }
        return cities;
    }

    @Override
    public List<City> getCitiesByDepartmentCode(String departmentCode) {
        List<City> cities = cityRepository.getCitiesByDepartmentCode(departmentCode);

        if (cities.isEmpty()) {
            throw new GlobalException.RessourceNotFoundException(
                    "No cities found in this region: " + departmentCode);
        }
        return cities;
    }
}
