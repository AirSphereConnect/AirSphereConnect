package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.AddressResponseDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.Address;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.User;
import org.springframework.stereotype.Component;

public class UserMapper {

    // Frontend -> Backend
    public static User toEntity(UserRequestDto request) {
        if (request == null) return null;

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        if (request.getAddress() != null) {
            Address address = new Address();
            address.setUser(user);
            address.setStreet(request.getAddress().getStreet());
            address.setCity(request.getAddress().getCity());
            user.setAddress(address);

        }
        return user;
    }

    // Backend -> Frontend
    public static UserResponseDto toDto(User user) {
        if (user == null) return null;

        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        if (user.getAddress() != null) {
            AddressResponseDto addressDto = new AddressResponseDto();
            Address address = user.getAddress();

            addressDto.setId(address.getId());
            addressDto.setStreet(address.getStreet());
            addressDto.setCreatedAt(address.getCreatedAt());
            addressDto.setUpdatedAt(address.getUpdatedAt());

            //Il faut remplacer City par CityResponseDto (/!\ bonne pratique)
            if (address.getCity() != null) {
                City city = new City();
                city.setId(address.getCity().getId());
                addressDto.setCity(city);
            }

            response.setAddress(addressDto);
        }
        return response;
    }

}