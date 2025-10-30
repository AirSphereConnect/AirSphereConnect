package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.AddressRequestDto;
import com.airSphereConnect.dtos.response.AddressResponseDto;
import com.airSphereConnect.dtos.response.CityIdResponseDto;
import com.airSphereConnect.entities.Address;
import com.airSphereConnect.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable("id") Long addressId,
            @Valid @RequestBody AddressRequestDto addressRequestDto) {

        Address updatedAddress = addressService.updateAddress(addressId, addressRequestDto);

        AddressResponseDto responseDto = new AddressResponseDto(
                updatedAddress.getId(),
                updatedAddress.getStreet(),
                updatedAddress.getCreatedAt(),
                new CityIdResponseDto(
                        updatedAddress.getCity().getId(),
                        updatedAddress.getCity().getName(),
                        updatedAddress.getCity().getPostalCode()
                )
        );

        return ResponseEntity.ok(responseDto);
    }
}

