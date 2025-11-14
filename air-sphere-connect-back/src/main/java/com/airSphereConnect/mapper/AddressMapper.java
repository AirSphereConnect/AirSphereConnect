package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.request.AddressRequestDto;
import com.airSphereConnect.dtos.response.AddressResponseDto;
import com.airSphereConnect.dtos.response.CityIdResponseDto;
import com.airSphereConnect.entities.Address;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.User;
import org.springframework.stereotype.Component;

/**
 * Mapper dédié pour convertir les entités Address vers les DTOs.
 */
@Component
public class AddressMapper {

    /**
     * Convertit un DTO AddressRequestDto en entité Address.
     *
     * @param addressRequestDto DTO adresse
     * @param user              entité User à rattacher à l'adresse
     * @return entité Address ou null si DTO null
     */
    public Address toEntity(AddressRequestDto addressRequestDto, User user) {
        if (addressRequestDto == null) return null;

        Address address = new Address();
        address.setUser(user);
        address.setStreet(addressRequestDto.getStreet());
        address.setCity(addressRequestDto.getCity());

        return address;
    }

    /**
     * Convertit une entité Address en AddressResponseDto.
     *
     * @param address entité Adresse
     * @return DTO Adresse
     */
    public AddressResponseDto toDto(Address address) {
        if (address == null) return null;

        AddressResponseDto addressDto = new AddressResponseDto();
        addressDto.setId(address.getId());
        addressDto.setStreet(address.getStreet());
        addressDto.setCreatedAt(address.getCreatedAt());
        addressDto.setUpdatedAt(address.getUpdatedAt());

        City city = address.getCity();
        if (city != null) {
            CityIdResponseDto cityDto = new CityIdResponseDto(city.getId(), city.getName(), city.getPostalCode());
            addressDto.setCity(cityDto);
        }

        return addressDto;
    }
}
