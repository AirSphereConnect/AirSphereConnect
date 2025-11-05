package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
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

    List<City> findDistinctByPopulations_PopulationGreaterThanEqual(Integer population);

    List<City> findDistinctByPopulations_PopulationLessThanEqual(Integer population);

    List<City> findDistinctByPopulations_PopulationBetween(Integer populationMin, Integer populationMax);

    List<City> findByNameContainingIgnoreCase(String query);

    boolean existsByInseeCode(String inseeCode);

    List<City> findByAreaCodeOrderByPopulationDesc(String areaCode, Pageable pageable);

}

