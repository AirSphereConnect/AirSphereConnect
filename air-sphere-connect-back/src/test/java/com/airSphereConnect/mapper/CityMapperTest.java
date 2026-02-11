package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.CityResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CityMapper Test Suite")
class CityMapperTest {

    private CityMapper cityMapper;

    @BeforeEach
    void setUp() {
        cityMapper = new CityMapper();
    }

    @Test
    @DisplayName("toDto should map all fields correctly")
    void toDto_shouldMapAllFields() {
        Department dept = new Department();
        dept.setName("Hérault");

        City city = new City();
        city.setId(1L);
        city.setInseeCode("34172");
        city.setName("Montpellier");
        city.setPostalCode("34000");
        city.setLatitude(43.6);
        city.setLongitude(3.86);
        city.setAreaCode("243400017");
        city.setDepartment(dept);
        city.setPopulation(290053);

        CityResponseDto dto = cityMapper.toDto(city);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.inseeCode()).isEqualTo("34172");
        assertThat(dto.name()).isEqualTo("Montpellier");
        assertThat(dto.postalCode()).isEqualTo("34000");
        assertThat(dto.latitude()).isEqualTo(43.6);
        assertThat(dto.longitude()).isEqualTo(3.86);
        assertThat(dto.areaCode()).isEqualTo("243400017");
        assertThat(dto.departmentName()).isEqualTo("Hérault");
        assertThat(dto.population()).isEqualTo(290053);
    }

    @Test
    @DisplayName("toDto should return null for null city")
    void toDto_shouldReturnNullForNullCity() {
        CityResponseDto dto = cityMapper.toDto(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("toDto should handle null department")
    void toDto_shouldHandleNullDepartment() {
        City city = new City();
        city.setId(1L);
        city.setName("Paris");
        city.setDepartment(null);

        CityResponseDto dto = cityMapper.toDto(city);

        assertThat(dto).isNotNull();
        assertThat(dto.departmentName()).isNull();
    }
}
