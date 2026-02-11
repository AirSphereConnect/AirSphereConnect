package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.City;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CityServiceImpl Test Suite")
class CityServiceImplTest {

    @Mock
    private CityRepository cityRepository;

    private CityServiceImpl cityService;

    @BeforeEach
    void setUp() {
        cityService = new CityServiceImpl(cityRepository);
    }

    @Nested
    @DisplayName("getAllCities tests")
    class GetAllCitiesTests {
        @Test
        @DisplayName("should return all cities")
        void shouldReturnAllCities() {
            City city = createCity();
            when(cityRepository.findAll()).thenReturn(List.of(city));

            List<City> result = cityService.getAllCities();

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getCityByName tests")
    class GetCityByNameTests {
        @Test
        @DisplayName("should return city when found")
        void shouldReturnCityWhenFound() {
            City city = createCity();
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));

            City result = cityService.getCityByName("Montpellier");

            assertThat(result.getName()).isEqualTo("Montpellier");
        }

        @Test
        @DisplayName("should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(cityRepository.findByNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cityService.getCityByName("Unknown"))
                    .isInstanceOf(GlobalException.ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getCityByInseeCode tests")
    class GetCityByInseeCodeTests {
        @Test
        @DisplayName("should return city when found")
        void shouldReturnCityWhenFound() {
            City city = createCity();
            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));

            City result = cityService.getCityByInseeCode("34172");

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(cityRepository.findByInseeCode("99999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cityService.getCityByInseeCode("99999"))
                    .isInstanceOf(GlobalException.ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getCitiesByPostalCode tests")
    class GetCitiesByPostalCodeTests {
        @Test
        @DisplayName("should return city when found")
        void shouldReturnCityWhenFound() {
            City city = createCity();
            when(cityRepository.findByPostalCode("34000")).thenReturn(Optional.of(city));

            City result = cityService.getCitiesByPostalCode("34000");

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(cityRepository.findByPostalCode("99999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cityService.getCitiesByPostalCode("99999"))
                    .isInstanceOf(GlobalException.ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getCitiesByRegionName tests")
    class GetCitiesByRegionNameTests {
        @Test
        @DisplayName("should return cities when found")
        void shouldReturnCitiesWhenFound() {
            City city = createCity();
            when(cityRepository.findByDepartmentRegionNameIgnoreCase("Occitanie"))
                    .thenReturn(List.of(city));

            List<City> result = cityService.getCitiesByRegionName("Occitanie");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should throw exception when none found")
        void shouldThrowExceptionWhenNoneFound() {
            when(cityRepository.findByDepartmentRegionNameIgnoreCase("Unknown"))
                    .thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> cityService.getCitiesByRegionName("Unknown"))
                    .isInstanceOf(GlobalException.ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getCitiesByDepartmentName tests")
    class GetCitiesByDepartmentNameTests {
        @Test
        @DisplayName("should return cities when found")
        void shouldReturnCitiesWhenFound() {
            City city = createCity();
            when(cityRepository.findByDepartmentNameIgnoreCase("Hérault"))
                    .thenReturn(List.of(city));

            List<City> result = cityService.getCitiesByDepartmentName("Hérault");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should throw exception when none found")
        void shouldThrowExceptionWhenNoneFound() {
            when(cityRepository.findByDepartmentNameIgnoreCase("Unknown"))
                    .thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> cityService.getCitiesByDepartmentName("Unknown"))
                    .isInstanceOf(GlobalException.ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getCitiesByDepartmentCode tests")
    class GetCitiesByDepartmentCodeTests {
        @Test
        @DisplayName("should return cities when found")
        void shouldReturnCitiesWhenFound() {
            City city = createCity();
            when(cityRepository.findByDepartmentCode("34")).thenReturn(List.of(city));

            List<City> result = cityService.getCitiesByDepartmentCode("34");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should throw exception when none found")
        void shouldThrowExceptionWhenNoneFound() {
            when(cityRepository.findByDepartmentCode("99")).thenReturn(Collections.emptyList());

            assertThatThrownBy(() -> cityService.getCitiesByDepartmentCode("99"))
                    .isInstanceOf(GlobalException.ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getCitiesByPopulation tests")
    class GetCitiesByPopulationTests {
        @Test
        @DisplayName("should return cities with population >= threshold")
        void shouldReturnCitiesWithPopulationGte() {
            City city = createCity();
            when(cityRepository.findDistinctByPopulations_PopulationGreaterThanEqual(100000))
                    .thenReturn(List.of(city));

            List<City> result = cityService.getCitiesByPopulationGreaterThanEqual(100000);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return cities with population <= threshold")
        void shouldReturnCitiesWithPopulationLte() {
            City city = createCity();
            when(cityRepository.findDistinctByPopulations_PopulationLessThanEqual(500000))
                    .thenReturn(List.of(city));

            List<City> result = cityService.getCitiesByPopulationLessThanEqual(500000);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return cities with population between range")
        void shouldReturnCitiesWithPopulationBetween() {
            City city = createCity();
            when(cityRepository.findDistinctByPopulations_PopulationBetween(100000, 500000))
                    .thenReturn(List.of(city));

            List<City> result = cityService.getCitiesByPopulationBetweenThan(100000, 500000);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should throw exception when min > max")
        void shouldThrowExceptionWhenMinGreaterThanMax() {
            assertThatThrownBy(() -> cityService.getCitiesByPopulationBetweenThan(500000, 100000))
                    .isInstanceOf(GlobalException.BadRequestException.class);
        }
    }

    @Nested
    @DisplayName("findByNameContainingIgnoreCase tests")
    class FindByNameContainingTests {
        @Test
        @DisplayName("should return matching cities")
        void shouldReturnMatchingCities() {
            City city = createCity();
            when(cityRepository.findByNameContainingIgnoreCase("mont")).thenReturn(List.of(city));

            List<City> result = cityService.findByNameContainingIgnoreCase("mont");

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getTopCitiesByAreaCode tests")
    class GetTopCitiesByAreaCodeTests {
        @Test
        @DisplayName("should return top cities by area code")
        void shouldReturnTopCities() {
            City city = createCity();
            when(cityRepository.findByAreaCodeOrderByPopulationDesc(anyString(), any(PageRequest.class)))
                    .thenReturn(List.of(city));

            List<City> result = cityService.getTopCitiesByAreaCode("34", 5);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findTop10ByNameStartingWithIgnoreCase tests")
    class FindTop10ByNameStartingWithTests {
        @Test
        @DisplayName("should return cities starting with query")
        void shouldReturnCitiesStartingWith() {
            City city = createCity();
            when(cityRepository.findTop10ByNameStartingWithIgnoreCase("Mont"))
                    .thenReturn(List.of(city));

            List<City> result = cityService.findTop10ByNameStartingWithIgnoreCase("Mont");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should complete with containing results if less than 10")
        void shouldCompleteWithContainingResults() {
            City city1 = createCity();
            City city2 = new City();
            city2.setId(2L);
            city2.setName("Clermont");

            when(cityRepository.findTop10ByNameStartingWithIgnoreCase("Mont"))
                    .thenReturn(new java.util.ArrayList<>(List.of(city1)));
            when(cityRepository.findTop10ByNameContainingIgnoreCase("Mont"))
                    .thenReturn(List.of(city1, city2));

            List<City> result = cityService.findTop10ByNameStartingWithIgnoreCase("Mont");

            assertThat(result).hasSize(2);
        }
    }

    // Helper
    private City createCity() {
        City city = new City();
        city.setId(1L);
        city.setName("Montpellier");
        city.setInseeCode("34172");
        city.setAreaCode("34");
        city.setPopulation(290000);
        return city;
    }
}
