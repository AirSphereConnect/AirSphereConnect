package com.airsphereconnect.repositories;

import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.Department;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByNameIgnoreCaseIn(Collection<String> name);

    Optional<City> findByInseeCode(String inseeCode);

    Optional<City> findByNameIgnoreCase(String name);

    Optional<City> findByPostalCode(String postalCode);

    Optional<City> findByNameIgnoreCaseAndDepartment(String name, Department department);

    List<City> findByDepartmentRegionNameIgnoreCase(String regionName);

    List<City> findByDepartmentNameIgnoreCase(String department);

    List<City> findByDepartmentCode(String department);

    List<City> findByDepartmentIn(List<Department> departments);

    List<City> findDistinctByPopulations_CountGreaterThanEqual(Integer population);

    List<City> findDistinctByPopulations_CountLessThanEqual(Integer population);

    List<City> findDistinctByPopulations_CountBetween(Integer populationMin, Integer populationMax);

    List<City> findByNameContainingIgnoreCase(String query);

    boolean existsByInseeCode(String inseeCode);

    List<City> findByAreaCode(String areaCode);

    List<City> findByAreaCodeOrderByPopulationDesc(String areaCode, Pageable pageable);

    List<City> findTop10ByNameStartingWithIgnoreCase(String query);

    List<City> findTop10ByNameContainingIgnoreCase(String query);
}

