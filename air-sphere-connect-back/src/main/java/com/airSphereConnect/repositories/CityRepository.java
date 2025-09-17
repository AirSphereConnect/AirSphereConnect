package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long>  {

    Optional<City> getCityByName(String name);
//    Optional<City> getCityByPostalCode(Long code);
    Optional<City> getCitybyRegion(String region);
//    Optional<City> getCityByDepartment(String department);

}

