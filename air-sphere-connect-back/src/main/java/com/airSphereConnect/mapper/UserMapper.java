package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.AlertsDto;
import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.AddressResponseDto;
import com.airSphereConnect.dtos.response.CityIdResponseDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.Address;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Favorite;
import com.airSphereConnect.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private static FavoriteMapper favoriteMapper;
    private static FavoritesAlertsMapper favoritesAlertsMapper;

    public UserMapper(FavoriteMapper favoriteMapper, FavoritesAlertsMapper favoritesAlertsMapper) {
        UserMapper.favoriteMapper = favoriteMapper;
        UserMapper.favoritesAlertsMapper = favoritesAlertsMapper;
    }

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
        if (user == null || user.getDeletedAt() != null) return null;

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

            if (address.getCity() != null) {
                City city = address.getCity();
                CityIdResponseDto cityDto = new CityIdResponseDto(city.getId(), city.getName(), city.getPostalCode());
                addressDto.setCity(cityDto);
            }

            response.setAddress(addressDto);
        }
        if (user.getFavorites() != null) {
            List<FavoriteDto> favoriteDtos = user.getFavorites().stream()
                    .filter(fav -> fav.getDeletedAt() == null)
                    .map(favoriteMapper::toDto)
                    .collect(Collectors.toList());
            response.setFavorites(favoriteDtos);
        }
        if (user.getFavoritesAlerts() != null) {
            List<FavoritesAlertsDto> favoritesAlertsDtos = user.getFavoritesAlerts().stream()
                    .filter(fav -> fav.getDeletedAt() == null)
                    .map(FavoritesAlertsMapper::toDto)
                    .collect(Collectors.toList());
            System.out.println("favoritesAlertsDtos: " + favoritesAlertsDtos);
            response.setAlerts(favoritesAlertsDtos);
        }


        return response;
    }

}