package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.request.AddressRequestDto;
import com.airSphereConnect.entities.Address;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.services.AddressService;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AddressController Test Suite")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("PUT /api/address/{id} should update address")
    void updateAddress_shouldReturn200() throws Exception {
        AddressRequestDto request = new AddressRequestDto();
        request.setStreet("10 Rue de la Paix");

        City city = new City();
        city.setId(1L);
        city.setName("Paris");
        city.setPostalCode("75001");

        Address response = new Address();
        response.setId(1L);
        response.setStreet("10 Rue de la Paix");
        response.setCity(city);

        when(addressService.updateAddress(eq(1L), any(AddressRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/address/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
