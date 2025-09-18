package com.airSphereConnect.mapper;


import com.airSphereConnect.dtos.request.AddressRequestDto;
import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.AddressResponseDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.Address;
import com.airSphereConnect.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Frontend -> Backend
    public User toEntity(UserRequestDto request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        if (request.getAddress() != null) {
            Address address = new Address();
            address.setUser(user);
            user.setAddress(address);

        }

        return user;
    }

    // Backend -> Frontend
    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        if (user.getAddress() != null) {
            AddressResponseDto addressDto = new AddressResponseDto();
            response.setAddress(addressDto);
        }

        return response;
    }

}