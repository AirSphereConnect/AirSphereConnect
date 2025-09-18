package com.airSphereConnect.services;

import com.airSphereConnect.entities.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserByUsername(String username);

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User newUserData);

    User deleteUser(Long id);
}
