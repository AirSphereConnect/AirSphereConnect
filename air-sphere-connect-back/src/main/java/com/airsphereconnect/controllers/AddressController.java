package com.airsphereconnect.controllers;

import com.airsphereconnect.dtos.request.AddressRequestDto;
import com.airsphereconnect.dtos.response.AddressResponseDto;
import com.airsphereconnect.dtos.response.CityIdResponseDto;
import com.airsphereconnect.entities.Address;
import com.airsphereconnect.services.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Adresses", description = "API de gestion des adresses utilisateurs")
@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(
            summary = "Mettre à jour une adresse",
            description = "Permet de modifier les informations d'une adresse existante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adresse mise à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddressResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Adresse non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @Parameter(description = "Identifiant de l'adresse", example = "1", required = true)
            @PathVariable("id") Long addressId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouvelles informations de l'adresse",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AddressRequestDto.class))
            )
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
