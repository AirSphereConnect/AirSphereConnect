package com.airsphereconnect.services;

import com.airsphereconnect.dtos.request.AddressRequestDto;
import com.airsphereconnect.entities.Address;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.repositories.AddressRepository;
import com.airsphereconnect.repositories.CityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Test Suite")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private City city;
    private AddressRequestDto requestDto;

    @BeforeEach
    void setUp() {
        city = new City();
        city.setId(1L);

        address = new Address();
        address.setStreet("1 rue de la Paix");
        address.setCity(city);

        requestDto = new AddressRequestDto("2 avenue du Général", city);
    }

    @Nested
    @DisplayName("Tests pour updateAddress")
    class UpdateAddressTests {

        @Test
        @DisplayName("Devrait mettre à jour une adresse avec succès (sans changer la ville)")
        void shouldUpdateAddressSuccessfullyWithoutCity() {
            AddressRequestDto noCity = new AddressRequestDto("Nouvelle rue", null);
            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
            when(addressRepository.save(address)).thenReturn(address);

            Address result = addressService.updateAddress(1L, noCity);

            assertNotNull(result);
            assertEquals("Nouvelle rue", result.getStreet());
            verify(cityRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Devrait mettre à jour une adresse avec changement de ville")
        void shouldUpdateAddressWithNewCity() {
            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
            when(cityRepository.findById(1L)).thenReturn(Optional.of(city));
            when(addressRepository.save(address)).thenReturn(address);

            Address result = addressService.updateAddress(1L, requestDto);

            assertNotNull(result);
            verify(cityRepository).findById(1L);
            verify(addressRepository).save(address);
        }

        @Test
        @DisplayName("Devrait échouer si l'adresse n'existe pas")
        void shouldThrowExceptionWhenAddressNotFound() {
            when(addressRepository.findById(999L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> addressService.updateAddress(999L, requestDto)
            );

            assertTrue(exception.getMessage().contains("Address not found with id 999"));
        }

        @Test
        @DisplayName("Devrait échouer si la ville n'existe pas")
        void shouldThrowExceptionWhenCityNotFound() {
            City unknownCity = new City();
            unknownCity.setId(999L);
            AddressRequestDto badCityDto = new AddressRequestDto("1 rue X", unknownCity);

            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
            when(cityRepository.findById(999L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> addressService.updateAddress(1L, badCityDto)
            );

            assertTrue(exception.getMessage().contains("City not found with id 999"));
        }
    }
}
