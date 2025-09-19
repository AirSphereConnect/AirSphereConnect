package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long>  {

    Optional<City> getCityByNameIgnoreCase(String name);
    Optional<City> getCityByPostalCode(String code);
    List<City> getCitiesByDepartmentRegionNameIgnoreCase(String regionName);
    List<City> getCitiesByDepartmentNameIgnoreCase(String department);
    List<City> getCitiesByDepartmentCode(String department);

}

