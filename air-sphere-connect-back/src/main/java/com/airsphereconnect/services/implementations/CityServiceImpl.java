package com.airsphereconnect.services.implementations;

import com.airsphereconnect.entities.City;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.repositories.CityRepository;
import com.airsphereconnect.services.CityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    private static final String NOT_FOUND_SUFFIX = " not found.";

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
                "City with name " + name + NOT_FOUND_SUFFIX));
    }



    @Override
    public City getCityByInseeCode(String inseeCode) {
        return cityRepository.findByInseeCode(inseeCode).orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                "City with INSEE code " + inseeCode + NOT_FOUND_SUFFIX));
    }

    @Override
    public City getCitiesByPostalCode(String code) {
        return cityRepository.findByPostalCode(String.valueOf(code)).orElseThrow(() -> new GlobalException.ResourceNotFoundException(
                "City with name " + code + NOT_FOUND_SUFFIX));
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
        return cityRepository.findDistinctByPopulations_CountGreaterThanEqual(population);
    }

    @Override
    public List<City> getCitiesByPopulationLessThanEqual(Integer population) {
        return cityRepository.findDistinctByPopulations_CountLessThanEqual(population);
    }

    @Override
    public List<City> getCitiesByPopulationBetweenThan(Integer populationMin, Integer populationMax) {
        if (populationMin > populationMax) {
            throw new GlobalException.BadRequestException("Minimum population cannot be greater than maximum population.");
        }
        return cityRepository.findDistinctByPopulations_CountBetween(populationMin, populationMax);
    }

    public List<City> findByNameContainingIgnoreCase(String query) {
        return cityRepository.findByNameContainingIgnoreCase(query);
    }

    @Override
    public List<City> getTopCitiesByAreaCode(String areaCode, int limit) {
        return cityRepository.findByAreaCodeOrderByPopulationDesc(areaCode, PageRequest.of(0, limit));
    }

    @Override
    public List<City> findTop10ByNameStartingWithIgnoreCase(String query) {
        List<City> startsWith = cityRepository.findTop10ByNameStartingWithIgnoreCase(query);

        if (startsWith.size() < 10) {
            List<City> contains = cityRepository.findTop10ByNameContainingIgnoreCase(query);
            contains.stream()
                    .filter(c -> !startsWith.contains(c))
                    .limit(10L - startsWith.size())
                    .forEach(startsWith::add);
        }


        return startsWith;
    }

}
