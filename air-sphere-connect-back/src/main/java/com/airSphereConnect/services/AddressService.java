package com.airSphereConnect.services;

import com.airSphereConnect.dtos.request.AddressRequestDto;
import com.airSphereConnect.entities.Address;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.repositories.AddressRepository;
import com.airSphereConnect.repositories.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;

    public AddressService(AddressRepository addressRepository, CityRepository cityRepository) {
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
    }

    public Address updateAddress(Long addressId, AddressRequestDto dto) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id " + addressId));

        address.setStreet(dto.getStreet());

        if (dto.getCity() != null) {
            City city = cityRepository.findById(dto.getCity().getId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found with id " + dto.getCity().getId()));
            address.setCity(city);
        }

        return addressRepository.save(address);
    }
}

