package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCode(String code);

    Optional<Region> findByNameIgnoreCase(String name);
}
