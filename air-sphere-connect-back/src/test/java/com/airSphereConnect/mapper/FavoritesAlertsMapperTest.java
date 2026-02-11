package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.FavoritesAlertsDto;
import com.airSphereConnect.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FavoritesAlertsMapper Test Suite")
class FavoritesAlertsMapperTest {

    private FavoritesAlertsMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FavoritesAlertsMapper();
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map DTO to entity correctly")
        void toEntity_shouldMapCorrectly() {
            FavoritesAlertsDto dto = new FavoritesAlertsDto();
            dto.setId(10L);
            dto.setEnabled(true);
            dto.setCityId(5L);

            FavoritesAlerts entity = FavoritesAlertsMapper.toEntity(1L, dto);

            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(10L);
            assertThat(entity.isEnabled()).isTrue();
            assertThat(entity.getUser().getId()).isEqualTo(1L);
            assertThat(entity.getCity().getId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("should return null for null DTO")
        void toEntity_shouldReturnNullForNullDto() {
            FavoritesAlerts entity = FavoritesAlertsMapper.toEntity(1L, null);
            assertThat(entity).isNull();
        }

        @Test
        @DisplayName("should handle null cityId")
        void toEntity_shouldHandleNullCityId() {
            FavoritesAlertsDto dto = new FavoritesAlertsDto();
            dto.setEnabled(false);
            dto.setCityId(null);

            FavoritesAlerts entity = FavoritesAlertsMapper.toEntity(1L, dto);

            assertThat(entity).isNotNull();
            assertThat(entity.getCity()).isNull();
        }
    }

    @Nested
    @DisplayName("toDto tests")
    class ToDtoTests {

        @Test
        @DisplayName("should map entity to DTO correctly")
        void toDto_shouldMapCorrectly() {
            User user = new User();
            user.setId(1L);

            City city = new City();
            city.setId(2L);
            city.setName("Montpellier");

            Department dept = new Department();
            dept.setId(3L);

            Region region = new Region();
            region.setId(4L);

            FavoritesAlerts entity = new FavoritesAlerts();
            entity.setId(10L);
            entity.setUser(user);
            entity.setCity(city);
            entity.setDepartment(dept);
            entity.setRegion(region);
            entity.setEnabled(true);

            FavoritesAlertsDto dto = mapper.toDto(entity);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(10L);
            assertThat(dto.getUser()).isEqualTo(1L);
            assertThat(dto.getCityName()).isEqualTo("Montpellier");
            assertThat(dto.getDepartmentId()).isEqualTo(3L);
            assertThat(dto.getRegionId()).isEqualTo(4L);
            assertThat(dto.getEnabled()).isTrue();
        }

        @Test
        @DisplayName("should return null for null entity")
        void toDto_shouldReturnNullForNullEntity() {
            FavoritesAlertsDto dto = mapper.toDto(null);
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("should handle null relationships")
        void toDto_shouldHandleNullRelationships() {
            FavoritesAlerts entity = new FavoritesAlerts();
            entity.setId(10L);
            entity.setUser(null);
            entity.setCity(null);
            entity.setDepartment(null);
            entity.setRegion(null);

            FavoritesAlertsDto dto = mapper.toDto(entity);

            assertThat(dto).isNotNull();
            assertThat(dto.getUser()).isNull();
            assertThat(dto.getCityName()).isNull();
            assertThat(dto.getDepartmentId()).isNull();
            assertThat(dto.getRegionId()).isNull();
        }
    }
}
