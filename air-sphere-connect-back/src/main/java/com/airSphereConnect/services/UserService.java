package com.airSphereConnect.services;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserByUsername(String username);

    User getUserById(Long id);

    UserResponseDto createUser(UserRequestDto userDto);

    User updateUser(Long id, User newUserData);

    void deleteUser(Long id);
}
