package com.airsphereconnect.repositories;

import com.airsphereconnect.entities.Alerts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Respository apporte juste de la clarté au code.
@Repository
public interface AlertsRepository extends JpaRepository<Alerts, Long> {
    List<Alerts> findByUserId(Long userId);
}

