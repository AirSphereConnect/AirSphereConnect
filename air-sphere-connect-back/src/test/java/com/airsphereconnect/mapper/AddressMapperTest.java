package com.airsphereconnect.mapper;

import com.airsphereconnect.dtos.request.AddressRequestDto;
import com.airsphereconnect.dtos.response.AddressResponseDto;
import com.airsphereconnect.entities.Address;
import com.airsphereconnect.entities.City;
import com.airsphereconnect.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AddressMapper Test Suite")
class AddressMapperTest {

    private AddressMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AddressMapper();
    }

    @Nested
    @DisplayName("toEntity tests")
    class ToEntityTests {

        @Test
        @DisplayName("should map request to entity correctly")
        void toEntity_shouldMapCorrectly() {
            City city = new City();
            city.setId(1L);
            city.setName("Montpellier");

            AddressRequestDto request = new AddressRequestDto();
            request.setStreet("10 Rue de la Paix");
            request.setCity(city);

            User user = new User();
            user.setId(1L);

            Address entity = mapper.toEntity(request, user);

            assertThat(entity).isNotNull();
            assertThat(entity.getStreet()).isEqualTo("10 Rue de la Paix");
            assertThat(entity.getCity()).isEqualTo(city);
            assertThat(entity.getUser()).isEqualTo(user);
        }

        @Test
        @DisplayName("should return null for null request")
        void toEntity_shouldReturnNullForNullRequest() {
            Address entity = mapper.toEntity(null, new User());
            assertThat(entity).isNull();
        }
    }

    @Nested
    @DisplayName("toDto tests")
    class ToDtoTests {

        @Test
        @DisplayName("should map entity to DTO correctly")
        void toDto_shouldMapCorrectly() {
            City city = new City();
            city.setId(1L);
            city.setName("Montpellier");
            city.setPostalCode("34000");

            LocalDateTime now = LocalDateTime.now();

            Address address = new Address();
            address.setId(10L);
            address.setStreet("10 Rue de la Paix");
            address.setCity(city);
            address.setCreatedAt(now);
            address.setUpdatedAt(now);

            AddressResponseDto dto = mapper.toDto(address);

            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(10L);
            assertThat(dto.getStreet()).isEqualTo("10 Rue de la Paix");
            assertThat(dto.getCity()).isNotNull();
            assertThat(dto.getCity().id()).isEqualTo(1L);
            assertThat(dto.getCity().name()).isEqualTo("Montpellier");
            assertThat(dto.getCity().postalCode()).isEqualTo("34000");
        }

        @Test
        @DisplayName("should return null for null entity")
        void toDto_shouldReturnNullForNullEntity() {
            AddressResponseDto dto = mapper.toDto(null);
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("should handle null city")
        void toDto_shouldHandleNullCity() {
            Address address = new Address();
            address.setId(10L);
            address.setStreet("10 Rue de la Paix");
            address.setCity(null);

            AddressResponseDto dto = mapper.toDto(address);

            assertThat(dto).isNotNull();
            assertThat(dto.getCity()).isNull();
        }
    }
}
