package com.airSphereConnect.controllers;

import com.airSphereConnect.dtos.response.CityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.mapper.CityMapper;
import com.airSphereConnect.services.CityService;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CityController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CityController Test Suite")
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CityService cityService;

    @MockitoBean
    private CityMapper cityMapper;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private City city;
    private CityResponseDto cityDto;

    @BeforeEach
    void setUp() {
        city = new City();
        city.setId(1L);
        city.setName("Montpellier");
        cityDto = new CityResponseDto(1L, "34172", "Montpellier", "34000", 43.6, 3.86, "243400017", "Hérault", 290053);
    }

    @Test
    @DisplayName("GET /api/cities should return list of cities")
    void getAllCities_shouldReturn200() throws Exception {
        when(cityService.getAllCities()).thenReturn(List.of(city));
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /api/cities/insee-code/{inseeCode} should return city")
    void getCityByInseeCode_shouldReturn200() throws Exception {
        when(cityService.getCityByInseeCode("34172")).thenReturn(city);
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities/insee-code/34172"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /api/cities/postal-code/{postalCode} should return city")
    void getCityByPostalCode_shouldReturn200() throws Exception {
        when(cityService.getCitiesByPostalCode("34000")).thenReturn(city);
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities/postal-code/34000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /api/cities/city?name= should return city")
    void getCityByName_shouldReturn200() throws Exception {
        when(cityService.getCityByName("Montpellier")).thenReturn(city);
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities/city").param("name", "Montpellier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /api/cities/search-name?query= should return matching cities")
    void searchCities_shouldReturn200() throws Exception {
        when(cityService.findTop10ByNameStartingWithIgnoreCase("Mon")).thenReturn(List.of(city));
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities/search-name").param("query", "Mon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /api/cities/region/{region} should return cities")
    void getCitiesByRegion_shouldReturn200() throws Exception {
        when(cityService.getCitiesByRegionName("Occitanie")).thenReturn(List.of(city));
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities/region/Occitanie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /api/cities/departmentName/{name} should return cities")
    void getCitiesByDepartmentName_shouldReturn200() throws Exception {
        when(cityService.getCitiesByDepartmentName("Hérault")).thenReturn(List.of(city));
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities/departmentName/Hérault"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Montpellier"));
    }

    @Test
    @DisplayName("GET /api/cities/departmentCode/{code} should return cities")
    void getCitiesByDepartmentCode_shouldReturn200() throws Exception {
        when(cityService.getCitiesByDepartmentCode("34")).thenReturn(List.of(city));
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities/departmentCode/34"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Montpellier"));
    }

    @Nested
    @DisplayName("Population search tests")
    class PopulationSearchTests {

        @Test
        @DisplayName("GET /api/cities/search with min and max should return filtered cities")
        void searchByPopulation_withMinAndMax() throws Exception {
            when(cityService.getCitiesByPopulationBetweenThan(100000, 500000)).thenReturn(List.of(city));
            when(cityMapper.toDto(city)).thenReturn(cityDto);

            mockMvc.perform(get("/api/cities/search")
                            .param("populationMin", "100000")
                            .param("populationMax", "500000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Montpellier"));
        }

        @Test
        @DisplayName("GET /api/cities/search with min only should return filtered cities")
        void searchByPopulation_withMinOnly() throws Exception {
            when(cityService.getCitiesByPopulationGreaterThanEqual(100000)).thenReturn(List.of(city));
            when(cityMapper.toDto(city)).thenReturn(cityDto);

            mockMvc.perform(get("/api/cities/search")
                            .param("populationMin", "100000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Montpellier"));
        }

        @Test
        @DisplayName("GET /api/cities/search with max only should return filtered cities")
        void searchByPopulation_withMaxOnly() throws Exception {
            when(cityService.getCitiesByPopulationLessThanEqual(500000)).thenReturn(List.of(city));
            when(cityMapper.toDto(city)).thenReturn(cityDto);

            mockMvc.perform(get("/api/cities/search")
                            .param("populationMax", "500000"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/cities/search with no params should return all cities")
        void searchByPopulation_noParams() throws Exception {
            when(cityService.getAllCities()).thenReturn(List.of(city));
            when(cityMapper.toDto(city)).thenReturn(cityDto);

            mockMvc.perform(get("/api/cities/search"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("GET /api/cities/area/{areaCode}/top/{limit} should return top cities")
    void getTopCitiesByArea_shouldReturn200() throws Exception {
        when(cityService.getTopCitiesByAreaCode("34", 5)).thenReturn(List.of(city));
        when(cityMapper.toDto(city)).thenReturn(cityDto);

        mockMvc.perform(get("/api/cities/area/34/top/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Montpellier"));
    }
}
