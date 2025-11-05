package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.services.CityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
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
        return cityRepository.findByNameIgnoreCase(name).orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                "City with name " + name + " not found."));
    }



    @Override
    public City getCityByInseeCode(String inseeCode) {
        return cityRepository.findByInseeCode(inseeCode).orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                "City with INSEE code " + inseeCode + " not found."));
    }

    @Override
    public City getCitiesByPostalCode(String code) {
        return cityRepository.findByPostalCode(String.valueOf(code)).orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                "City with name " + code + " not found."));
    }

    @Override
    public List<City> getCitiesByRegionName(String regionName) {
        List<City> cities = cityRepository.findByDepartmentRegionNameIgnoreCase(regionName);

        if (cities.isEmpty()) {
            throw new GlobalException.ResourceNotFoundException(
                    "No cities found in this region: " + regionName);
        }
        return cities;
    }

    @Override
    public List<City> getCitiesByDepartmentName(String departmentName) {
        List<City> cities = cityRepository.findByDepartmentNameIgnoreCase(departmentName);

        if (cities.isEmpty()) {
            throw new GlobalException.ResourceNotFoundException(
                    "No cities found in this region: " + departmentName);
        }
        return cities;
    }

    @Override
    public List<City> getCitiesByDepartmentCode(String departmentCode) {
        List<City> cities = cityRepository.findByDepartmentCode(departmentCode);

        if (cities.isEmpty()) {
            throw new GlobalException.ResourceNotFoundException(
                    "No cities found in this region: " + departmentCode);
        }
        return cities;
    }

    @Override
    public List<City> getCitiesByPopulationGreaterThanEqual(Integer population) {
        return cityRepository.findDistinctByPopulations_PopulationGreaterThanEqual(population);
    }

    @Override
    public List<City> getCitiesByPopulationLessThanEqual(Integer population) {
        return cityRepository.findDistinctByPopulations_PopulationLessThanEqual(population);
    }

    @Override
    public List<City> getCitiesByPopulationBetweenThan(Integer populationMin, Integer populationMax) {
        if (populationMin > populationMax) {
            throw new GlobalException.BadRequestException("Minimum population cannot be greater than maximum population.");
        }
        return cityRepository.findDistinctByPopulations_PopulationBetween(populationMin, populationMax);
    }

    public List<City> findByNameContainingIgnoreCase(String query) {
        return cityRepository.findByNameContainingIgnoreCase(query);
    }

    @Override
    public List<City> getTopCitiesByAreaCode(String areaCode, int limit) {
        return cityRepository.findByAreaCodeOrderByPopulationDesc(areaCode, PageRequest.of(0, limit));
    }

}
