package com.airSphereConnect.services;

import com.airSphereConnect.entities.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    User getUserByUsername(String username);

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User newUserData);

    User deleteUser(Long id);

    Optional<User> findByUsername(String username) throws UsernameNotFoundException;
}
