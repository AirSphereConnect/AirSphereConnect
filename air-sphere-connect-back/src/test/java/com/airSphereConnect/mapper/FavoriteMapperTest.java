package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.FavoriteDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.Favorite;
import com.airSphereConnect.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FavoriteMapper Test Suite")
class FavoriteMapperTest {

    private FavoriteMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FavoriteMapper();
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

            Favorite favorite = new Favorite();
            favorite.setId(10L);
            favorite.setSelectAirQuality(true);
            favorite.setSelectPopulation(false);
            favorite.setSelectWeather(true);
            favorite.setUser(user);
            favorite.setCity(city);

            FavoriteDto dto = mapper.toDto(favorite);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(10L);
            assertThat(dto.getSelectAirQuality()).isTrue();
            assertThat(dto.getSelectPopulation()).isFalse();
            assertThat(dto.getSelectWeather()).isTrue();
            assertThat(dto.getUserId()).isEqualTo(1L);
            assertThat(dto.getCityId()).isEqualTo(2L);
            assertThat(dto.getCityName()).isEqualTo("Montpellier");
        }

        @Test
        @DisplayName("should handle null user and city")
        void toDto_shouldHandleNullUserAndCity() {
            Favorite favorite = new Favorite();
            favorite.setId(10L);
            favorite.setSelectAirQuality(false);
            favorite.setSelectPopulation(false);
            favorite.setSelectWeather(false);
            favorite.setUser(null);
            favorite.setCity(null);

            FavoriteDto dto = mapper.toDto(favorite);

            assertThat(dto).isNotNull();
            assertThat(dto.getUserId()).isNull();
            assertThat(dto.getCityId()).isNull();
            assertThat(dto.getCityName()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map DTO to entity correctly")
        void toEntity_shouldMapCorrectly() {
            FavoriteDto dto = new FavoriteDto();
            dto.setId(10L);
            dto.setSelectAirQuality(true);
            dto.setSelectPopulation(false);
            dto.setSelectWeather(true);

            Favorite entity = mapper.toEntity(dto);

            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(10L);
            assertThat(entity.getSelectAirQuality()).isTrue();
            assertThat(entity.getSelectPopulation()).isFalse();
            assertThat(entity.getSelectWeather()).isTrue();
        }
    }
}
