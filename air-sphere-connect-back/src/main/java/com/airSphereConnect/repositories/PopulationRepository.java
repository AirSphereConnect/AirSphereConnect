package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.Population;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopulationRepository extends JpaRepository<Population, Long> {

    // all the population of a city ordered by year desc
    List<Population> findByCityNameIgnoreCaseOrderByYearAsc(String cityName);
}
