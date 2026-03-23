package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.AlertsDto;
import com.airsphereconnect.entities.*;
import com.airsphereconnect.entities.enums.AlertType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AlertsMapper Test Suite")
class AlertsMapperTest {

    private AlertsMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AlertsMapper();
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
            city.setId(1L);
            city.setName("Montpellier");

            Region region = new Region();
            region.setId(1L);
            region.setName("Occitanie");

            LocalDateTime now = LocalDateTime.now();

            Alerts alert = new Alerts();
            alert.setId(10L);
            alert.setAlertType(AlertType.AIR_QUALITY);
            alert.setMessage("High PM2.5 levels");
            alert.setSentAt(now);
            alert.setCity(city);
            alert.setRegion(region);
            alert.setUser(user);

            AlertsDto dto = mapper.toDto(alert);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(10L);
            assertThat(dto.getAlertType()).isEqualTo(AlertType.AIR_QUALITY);
            assertThat(dto.getMessage()).isEqualTo("High PM2.5 levels");
            assertThat(dto.getSentAt()).isEqualTo(now);
            assertThat(dto.getCity()).isEqualTo(city);
            assertThat(dto.getRegion()).isEqualTo(region);
            assertThat(dto.getUserId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("should return null for null entity")
        void toDto_shouldReturnNullForNullEntity() {
            AlertsDto dto = mapper.toDto(null);
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("should handle null user")
        void toDto_shouldHandleNullUser() {
            Alerts alert = new Alerts();
            alert.setId(10L);
            alert.setUser(null);

            AlertsDto dto = mapper.toDto(alert);

            assertThat(dto).isNotNull();
            assertThat(dto.getUserId()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map DTO to entity correctly")
        void toEntity_shouldMapCorrectly() {
            City city = new City();
            city.setId(1L);
            city.setName("Paris");

            Region region = new Region();
            region.setId(1L);
            region.setName("Île-de-France");

            LocalDateTime now = LocalDateTime.now();

            AlertsDto dto = new AlertsDto();
            dto.setId(10L);
            dto.setAlertType(AlertType.WEATHER);
            dto.setMessage("Storm warning");
            dto.setSentAt(now);
            dto.setCity(city);
            dto.setRegion(region);

            Alerts entity = mapper.toEntity(dto);

            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(10L);
            assertThat(entity.getAlertType()).isEqualTo(AlertType.WEATHER);
            assertThat(entity.getMessage()).isEqualTo("Storm warning");
            assertThat(entity.getSentAt()).isEqualTo(now);
            assertThat(entity.getCity()).isEqualTo(city);
            assertThat(entity.getRegion()).isEqualTo(region);
        }

        @Test
        @DisplayName("should return null for null DTO")
        void toEntity_shouldReturnNullForNullDto() {
            Alerts entity = mapper.toEntity(null);
            assertThat(entity).isNull();
        }
    }
}
