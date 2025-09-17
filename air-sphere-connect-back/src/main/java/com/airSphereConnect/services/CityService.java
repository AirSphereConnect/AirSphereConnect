package com.airSphereConnect.services;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;

public interface CityService {

    City getCityByName(String name);

    City getCityByPostalCode(String code);

    City getCityByRegion(Region region);

    City getCityByDepartment(Department department);

}