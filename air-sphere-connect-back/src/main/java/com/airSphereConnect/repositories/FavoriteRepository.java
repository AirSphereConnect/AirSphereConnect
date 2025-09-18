package com.airSphereConnect.repositories;

import com.airSphereConnect.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByDeleteAtIsNull();

}
