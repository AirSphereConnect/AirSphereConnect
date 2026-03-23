package com.airsphereconnect.repositories;

import com.airsphereconnect.entities.Address;
import com.airsphereconnect.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Récupérer l'adresse d'un utilisateur
    Optional<Address> findByUser(User user);

    // Supprimer une adresse par utilisateur
    void deleteByUser(User user);
}
