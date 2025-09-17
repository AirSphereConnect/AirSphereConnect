package com.airSphereConnect.services;

import com.airSphereConnect.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserById(Long id);

    User createUser(User user);

    Optional<User> updateUser(Long id, User newUserData);

    boolean deleteUser(Long id);
}
