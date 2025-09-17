package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.services.CityService;
import com.airSphereConnect.services.DepartmentService;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public City getCityByName(String name) {
      return cityRepository.getCityByName(name).orElseThrow( () -> new GlobalException.RessourceNotFoundException(
              "City with name " + name + " not found."));
    }

    @Override
    public City getCityByPostalCode(String code) {
        return cityRepository.getCityByPostalCode(String.valueOf(code)).orElseThrow(() -> new GlobalException.RessourceNotFoundException(
                "City with name " + code + " not found."));

    }

    @Override
    public City getCityByRegion(Region region) {
        return null;
    }

    @Override
    public City getCityByDepartment(Department department) {
        return null;
    }
}
