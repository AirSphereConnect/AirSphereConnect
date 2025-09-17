package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.DepartmentRequestDto;
import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.Department;
import com.airSphereConnect.entities.User;

public class UserMapper {
    // RequestDto -> Entity
    public static UserRequestDto toDto(User user) {
        if (user == null) return null;

        return new UserRequestDto(
                user.getUsername(),
                user.getEmail()
                // à voir pour le password
        );
    }

    // Entity -> ResponseDto
    public static User toEntity(UserRequestDto userDto) {
        if (userDto == null) return null;

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        // à voir pour le password
        return user;
    }

    // Post UserResponseDto -> User
    public static UserResponseDto toResponseDto(User user) {
        if (user == null) return null;
        return new UserResponseDto(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                null // Ne pas exposer password
        );
    }

}
