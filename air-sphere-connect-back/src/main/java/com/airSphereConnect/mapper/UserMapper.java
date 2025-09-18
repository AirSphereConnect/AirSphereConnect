package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;

public interface UserMapper {
    // Entity -> ResponseDto
    public static UserResponseDto toResponseDto(User user) {
        if (user == null) return null;
        System.out.printf("UserMapper.toResponseDto(): user=%s\n", user);
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
                // pas de mot de passe ici !
        );
    }

    // RequestDto -> Entity
    public static User toEntity(UserRequestDto userDto) {
        if (userDto == null) return null;
        System.out.printf("UserMapper.toEntity: userDto=%s\n", userDto);
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword()); // le service doit encoder le mot de passe après !
        return user;
    }
}

