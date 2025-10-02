package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

<<<<<<< Updated upstream
    Optional<Region> getRegionByCode(String code);
=======
    List<Region> findByCode(String code);
>>>>>>> Stashed changes

    Optional<Region> getRegionByNameIgnoreCase(String name);
}
