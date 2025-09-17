package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long>  {

    Optional<City> getCityByName(String name);
    Optional<City> getCityByPostalCode(String code);
    Optional<City> getCityByDepartmentRegion(Region region);
    Optional<City> getCityByDepartment(Department department);

}

