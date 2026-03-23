package com.airsphereconnect.repositories;

import com.airsphereconnect.entities.Population;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopulationRepository extends JpaRepository<Population, Long> {

    // all the population of a city ordered by year desc
    List<Population> findByCityNameIgnoreCaseOrderByYearAsc(String cityName);
}
