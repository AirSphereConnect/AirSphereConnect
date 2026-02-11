package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.response.AirQualityDataResponseDto;
import com.airSphereConnect.dtos.response.AirQualityIndexResponseDto;
import com.airSphereConnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airSphereConnect.dtos.response.AirQualityStationResponseDto;
import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.AirQualityMapper;
import com.airSphereConnect.repositories.AirQualityIndexRepository;
import com.airSphereConnect.repositories.AirQualityMeasurementRepository;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AirQualityServiceImpl Test Suite")
class AirQualityServiceImplTest {

    @Mock
    private AirQualityStationRepository stationRepository;
    @Mock
    private AirQualityMeasurementRepository measurementRepository;
    @Mock
    private AirQualityIndexRepository indexRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private AirQualityMapper mapper;

    @InjectMocks
    private AirQualityServiceImpl airQualityService;

    private City city;
    private AirQualityStation station;
    private AirQualityMeasurement measurement;
    private AirQualityIndex index;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        city = new City();
        city.setId(1L);
        city.setName("Montpellier");
        city.setInseeCode("34172");
        city.setAreaCode("243400017");
        city.setPostalCode("34000");

        station = new AirQualityStation();
        station.setId(1L);
        station.setName("Station Montpellier");
        station.setCity(city);

        measurement = new AirQualityMeasurement();
        measurement.setId(1L);
        measurement.setPm10(25.0);
        measurement.setPm25(15.0);
        measurement.setNo2(30.0);
        measurement.setO3(50.0);
        measurement.setSo2(5.0);
        measurement.setUnit("µg/m³");
        measurement.setMeasuredAt(now);
        measurement.setStation(station);

        index = new AirQualityIndex();
        index.setId(1L);
        index.setQualityIndex(3);
        index.setQualityLabel("Moyen");
        index.setQualityColor("#FFFF00");
        index.setAreaCode("243400017");
        index.setAreaName("Montpellier Méditerranée");
        index.setSource("ATMO Occitanie");
        index.setMeasuredAt(now);
    }

    @Nested
    @DisplayName("getAllStations")
    class GetAllStationsTests {

        @Test
        @DisplayName("should return all stations mapped to DTOs")
        void shouldReturnAllStations() {
            AirQualityStationResponseDto stationDto = new AirQualityStationResponseDto(
                    1L, "Station Montpellier", "ATMO-34", "243400017", "Montpellier");

            when(stationRepository.findAll()).thenReturn(List.of(station));
            when(mapper.toDto(station)).thenReturn(stationDto);

            List<AirQualityStationResponseDto> result = airQualityService.getAllStations();

            assertEquals(1, result.size());
            assertEquals("Station Montpellier", result.get(0).name());
            verify(stationRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no stations")
        void shouldReturnEmptyList() {
            when(stationRepository.findAll()).thenReturn(List.of());

            List<AirQualityStationResponseDto> result = airQualityService.getAllStations();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getLatestMeasurementForCity")
    class GetLatestMeasurementTests {

        @Test
        @DisplayName("should return measurement found by INSEE code")
        void shouldReturnMeasurementByInseeCode() {
            AirQualityMeasurementResponseDto dto = new AirQualityMeasurementResponseDto(
                    1L, 25.0, 15.0, 30.0, 50.0, 5.0, "µg/m³", now, "Station Montpellier", "exact", null);

            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(dto);

            AirQualityMeasurementResponseDto result = airQualityService.getLatestMeasurementForCity("Montpellier");

            assertNotNull(result);
            assertEquals(25.0, result.pm10());
            assertEquals("exact", result.dataSource());
        }

        @Test
        @DisplayName("should fallback to areaCode when INSEE not found")
        void shouldFallbackToAreaCode() {
            AirQualityMeasurementResponseDto dto = new AirQualityMeasurementResponseDto(
                    1L, 25.0, 15.0, 30.0, 50.0, 5.0, "µg/m³", now, "Station Montpellier", "exact", null);

            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.empty());
            when(measurementRepository.findTopByStation_City_AreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(dto);

            AirQualityMeasurementResponseDto result = airQualityService.getLatestMeasurementForCity("Montpellier");

            assertNotNull(result);
            verify(measurementRepository).findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172");
            verify(measurementRepository).findTopByStation_City_AreaCodeOrderByMeasuredAtDesc("243400017");
        }

        @Test
        @DisplayName("should fallback to department when INSEE and areaCode not found")
        void shouldFallbackToDepartment() {
            AirQualityMeasurementResponseDto dto = new AirQualityMeasurementResponseDto(
                    1L, 25.0, 15.0, 30.0, 50.0, 5.0, "µg/m³", now, "Station", "department", "Nîmes");

            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.empty());
            when(measurementRepository.findTopByStation_City_AreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.empty());
            when(measurementRepository.findLatestByDepartmentCode("34"))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(eq(measurement), eq("department"), anyString())).thenReturn(dto);

            AirQualityMeasurementResponseDto result = airQualityService.getLatestMeasurementForCity("Montpellier");

            assertNotNull(result);
            assertEquals("department", result.dataSource());
        }

        @Test
        @DisplayName("should throw when city not found")
        void shouldThrowWhenCityNotFound() {
            when(cityRepository.findByNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

            assertThrows(GlobalException.ResourceNotFoundException.class,
                    () -> airQualityService.getLatestMeasurementForCity("Unknown"));
        }

        @Test
        @DisplayName("should throw when no measurement found anywhere")
        void shouldThrowWhenNoMeasurementFound() {
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.empty());
            when(measurementRepository.findTopByStation_City_AreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.empty());
            when(measurementRepository.findLatestByDepartmentCode("34"))
                    .thenReturn(List.of());

            GlobalException.ResourceNotFoundException exception = assertThrows(
                    GlobalException.ResourceNotFoundException.class,
                    () -> airQualityService.getLatestMeasurementForCity("Montpellier"));

            assertTrue(exception.getMessage().contains("Montpellier"));
        }

        @Test
        @DisplayName("should handle city with null areaCode and fallback to department")
        void shouldHandleCityWithNullAreaCodeFallbackToDepartment() {
            city.setAreaCode(null);
            AirQualityMeasurementResponseDto dto = new AirQualityMeasurementResponseDto(
                    1L, 25.0, 15.0, 30.0, 50.0, 5.0, "µg/m³", now, "Station", "department", "Other City");

            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.empty());
            when(measurementRepository.findLatestByDepartmentCode("34"))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(eq(measurement), eq("department"), anyString())).thenReturn(dto);

            AirQualityMeasurementResponseDto result = airQualityService.getLatestMeasurementForCity("Montpellier");

            assertNotNull(result);
            assertEquals("department", result.dataSource());
        }
    }

    @Nested
    @DisplayName("getLatestIndexQualityForCity")
    class GetLatestIndexTests {

        @Test
        @DisplayName("should return latest index for city")
        void shouldReturnLatestIndex() {
            AirQualityIndexResponseDto dto = new AirQualityIndexResponseDto(
                    1L, 3, "Moyen", "#FFFF00", now, "243400017", "Montpellier Méditerranée",
                    "ATMO Occitanie", "Qualité de l'air moyenne");

            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(index));
            when(mapper.toDto(eq(index), anyString())).thenReturn(dto);

            AirQualityIndexResponseDto result = airQualityService.getLatestIndexQualityForCity("Montpellier");

            assertNotNull(result);
            assertEquals(3, result.qualityIndex());
        }

        @Test
        @DisplayName("should throw when city has no areaCode")
        void shouldThrowWhenNoAreaCode() {
            city.setAreaCode(null);
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));

            assertThrows(GlobalException.ResourceNotFoundException.class,
                    () -> airQualityService.getLatestIndexQualityForCity("Montpellier"));
        }

        @Test
        @DisplayName("should throw when no index found")
        void shouldThrowWhenNoIndex() {
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.empty());

            assertThrows(GlobalException.ResourceNotFoundException.class,
                    () -> airQualityService.getLatestIndexQualityForCity("Montpellier"));
        }
    }

    @Nested
    @DisplayName("getMeasurementsHistoryForCity")
    class GetMeasurementsHistoryTests {

        @Test
        @DisplayName("should return history by INSEE code")
        void shouldReturnHistoryByInseeCode() {
            AirQualityMeasurementResponseDto dto = new AirQualityMeasurementResponseDto(
                    1L, 25.0, 15.0, 30.0, 50.0, 5.0, "µg/m³", now, "Station", "exact", null);
            LocalDate start = LocalDate.now().minusDays(7);
            LocalDate end = LocalDate.now();

            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(dto);

            List<AirQualityMeasurementResponseDto> result =
                    airQualityService.getMeasurementsHistoryForCity("Montpellier", start, end);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("should return empty list when no history found")
        void shouldReturnEmptyListWhenNoHistory() {
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(measurementRepository.findByStation_City_AreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(measurementRepository.findByDepartmentCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            List<AirQualityMeasurementResponseDto> result =
                    airQualityService.getMeasurementsHistoryForCity("Montpellier", null, null);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should use default date range when dates are null")
        void shouldUseDefaultDateRange() {
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(measurementRepository.findByStation_City_AreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(measurementRepository.findByDepartmentCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            airQualityService.getMeasurementsHistoryForCity("Montpellier", null, null);

            verify(measurementRepository).findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Nested
    @DisplayName("getIndicesHistoryForCity")
    class GetIndicesHistoryTests {

        @Test
        @DisplayName("should return indices history")
        void shouldReturnIndicesHistory() {
            AirQualityIndexResponseDto dto = new AirQualityIndexResponseDto(
                    1L, 3, "Moyen", "#FFFF00", now, "243400017", "Montpellier",
                    "ATMO Occitanie", "alert");

            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(index));
            when(mapper.toDto(eq(index), anyString())).thenReturn(dto);

            List<AirQualityIndexResponseDto> result =
                    airQualityService.getIndicesHistoryForCity("Montpellier", null, null);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("should throw when city has no areaCode")
        void shouldThrowWhenNoAreaCode() {
            city.setAreaCode(null);
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));

            assertThrows(GlobalException.ResourceNotFoundException.class,
                    () -> airQualityService.getIndicesHistoryForCity("Montpellier", null, null));
        }
    }

    @Nested
    @DisplayName("getCompleteDataForCity")
    class GetCompleteDataTests {

        @Test
        @DisplayName("should return complete data for city")
        void shouldReturnCompleteData() {
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(index));
            when(mapper.toDto(eq(index), any())).thenReturn(
                    new AirQualityIndexResponseDto(1L, 3, "Moyen", "#FFFF00", now,
                            "243400017", "Montpellier", "ATMO", "alert"));
            when(measurementRepository.findByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(
                    new AirQualityMeasurementResponseDto(1L, 25.0, 15.0, 30.0, 50.0, 5.0,
                            "µg/m³", now, "Station", "exact", null));

            // For history calls
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(measurement));
            when(indexRepository.findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(index));

            AirQualityDataResponseDto result = airQualityService.getCompleteDataForCity("Montpellier");

            assertNotNull(result);
            assertEquals("Montpellier", result.getCityName());
            assertEquals(1L, result.getCityId());
            assertEquals("34000", result.getPostalCode());
            assertEquals(3, result.getQualityIndex());
            assertEquals(25.0, result.getPm10());
        }

        @Test
        @DisplayName("should throw when city not found")
        void shouldThrowWhenCityNotFound() {
            when(cityRepository.findByNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

            assertThrows(GlobalException.ResourceNotFoundException.class,
                    () -> airQualityService.getCompleteDataForCity("Unknown"));
        }

        @Test
        @DisplayName("should fallback to areaCode when no measurements by INSEE")
        void shouldFallbackToAreaCodeForMeasurements() {
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(index));
            when(mapper.toDto(eq(index), any())).thenReturn(
                    new AirQualityIndexResponseDto(1L, 3, "Moyen", "#FFFF00", now,
                            "243400017", "Montpellier", "ATMO", "alert"));
            // No measurements by INSEE code
            when(measurementRepository.findByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(List.of());
            // But found by areaCode
            when(measurementRepository.findByStation_City_AreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(
                    new AirQualityMeasurementResponseDto(1L, 25.0, 15.0, 30.0, 50.0, 5.0,
                            "µg/m³", now, "Station", "exact", null));
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(measurementRepository.findByStation_City_AreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(measurement));
            when(indexRepository.findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(index));

            AirQualityDataResponseDto result = airQualityService.getCompleteDataForCity("Montpellier");

            assertNotNull(result);
            assertEquals(25.0, result.getPm10());
        }
    }

    @Nested
    @DisplayName("getTopCitiesWithDataInDepartment")
    class GetTopCitiesWithDataTests {

        @Test
        @DisplayName("should return top cities with measurements by INSEE code")
        void shouldReturnTopCitiesWithMeasurementsByInseeCode() {
            // city2 is in same department (34) but has no measurements
            City city2 = new City();
            city2.setId(2L);
            city2.setName("Béziers");
            city2.setInseeCode("34032");
            city2.setAreaCode("243400018");
            city2.setPostalCode("34500");
            city2.setPopulation(80000);

            city.setPopulation(290000);

            when(cityRepository.findAll()).thenReturn(List.of(city, city2));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.of(measurement));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34032"))
                    .thenReturn(Optional.empty());
            when(measurementRepository.findTopByStation_City_AreaCodeOrderByMeasuredAtDesc("243400018"))
                    .thenReturn(Optional.empty());

            // Setup for getCompleteDataForCity call
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(index));
            when(mapper.toDto(eq(index), any())).thenReturn(
                    new AirQualityIndexResponseDto(1L, 3, "Moyen", "#FFFF00", now,
                            "243400017", "Montpellier", "ATMO", null));
            when(measurementRepository.findByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(
                    new AirQualityMeasurementResponseDto(1L, 25.0, 15.0, 30.0, 50.0, 5.0,
                            "µg/m³", now, "Station", "exact", null));
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(measurement));
            when(indexRepository.findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(index));

            List<AirQualityDataResponseDto> result = airQualityService.getTopCitiesWithDataInDepartment("34", 5);

            assertEquals(1, result.size());
            assertEquals("Montpellier", result.get(0).getCityName());
        }

        @Test
        @DisplayName("should return empty list when no cities match department")
        void shouldReturnEmptyListWhenNoCitiesMatchDepartment() {
            when(cityRepository.findAll()).thenReturn(List.of(city));

            List<AirQualityDataResponseDto> result = airQualityService.getTopCitiesWithDataInDepartment("99", 5);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should fallback to areaCode when no measurements by INSEE")
        void shouldFallbackToAreaCodeWhenNoMeasurementsByInsee() {
            city.setPopulation(290000);

            when(cityRepository.findAll()).thenReturn(List.of(city));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.empty());
            when(measurementRepository.findTopByStation_City_AreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(measurement));

            // Setup for getCompleteDataForCity
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(index));
            when(mapper.toDto(eq(index), any())).thenReturn(
                    new AirQualityIndexResponseDto(1L, 3, "Moyen", "#FFFF00", now,
                            "243400017", "Montpellier", "ATMO", null));
            when(measurementRepository.findByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(List.of());
            when(measurementRepository.findByStation_City_AreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(
                    new AirQualityMeasurementResponseDto(1L, 25.0, 15.0, 30.0, 50.0, 5.0,
                            "µg/m³", now, "Station", "exact", null));
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(measurementRepository.findByStation_City_AreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(measurement));
            when(indexRepository.findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(index));

            List<AirQualityDataResponseDto> result = airQualityService.getTopCitiesWithDataInDepartment("34", 5);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("should sort cities by population descending")
        void shouldSortCitiesByPopulationDescending() {
            City smallCity = new City();
            smallCity.setId(2L);
            smallCity.setName("Béziers");
            smallCity.setInseeCode("34032");
            smallCity.setAreaCode("243400018");
            smallCity.setPostalCode("34500");
            smallCity.setPopulation(80000);

            city.setPopulation(290000);

            AirQualityMeasurement measurement2 = new AirQualityMeasurement();
            measurement2.setId(2L);
            measurement2.setStation(station);

            when(cityRepository.findAll()).thenReturn(List.of(smallCity, city)); // Small first in list
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.of(measurement));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34032"))
                    .thenReturn(Optional.of(measurement2));

            // Setup for Montpellier (larger population, should be first)
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(index));
            when(mapper.toDto(eq(index), any())).thenReturn(
                    new AirQualityIndexResponseDto(1L, 3, "Moyen", "#FFFF00", now,
                            "243400017", "Montpellier", "ATMO", null));
            when(measurementRepository.findByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(
                    new AirQualityMeasurementResponseDto(1L, 25.0, 15.0, 30.0, 50.0, 5.0,
                            "µg/m³", now, "Station", "exact", null));
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(measurement));
            when(indexRepository.findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(index));

            List<AirQualityDataResponseDto> result = airQualityService.getTopCitiesWithDataInDepartment("34", 1);

            assertEquals(1, result.size());
            assertEquals("Montpellier", result.get(0).getCityName()); // Larger city should be first
        }

        @Test
        @DisplayName("should handle cities with null population")
        void shouldHandleCitiesWithNullPopulation() {
            city.setPopulation(null);

            when(cityRepository.findAll()).thenReturn(List.of(city));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.of(measurement));

            // Setup for getCompleteDataForCity
            when(cityRepository.findByNameIgnoreCase("Montpellier")).thenReturn(Optional.of(city));
            when(indexRepository.findFirstByAreaCodeOrderByMeasuredAtDesc("243400017"))
                    .thenReturn(Optional.of(index));
            when(mapper.toDto(eq(index), any())).thenReturn(
                    new AirQualityIndexResponseDto(1L, 3, "Moyen", "#FFFF00", now,
                            "243400017", "Montpellier", "ATMO", null));
            when(measurementRepository.findByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(List.of(measurement));
            when(mapper.toDto(measurement)).thenReturn(
                    new AirQualityMeasurementResponseDto(1L, 25.0, 15.0, 30.0, 50.0, 5.0,
                            "µg/m³", now, "Station", "exact", null));
            when(measurementRepository.findByStation_City_InseeCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("34172"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(measurement));
            when(indexRepository.findByAreaCodeAndMeasuredAtBetweenOrderByMeasuredAtDesc(
                    eq("243400017"), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(index));

            List<AirQualityDataResponseDto> result = airQualityService.getTopCitiesWithDataInDepartment("34", 5);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("should skip cities with null INSEE code")
        void shouldSkipCitiesWithNullInseeCode() {
            city.setInseeCode(null);

            when(cityRepository.findAll()).thenReturn(List.of(city));

            List<AirQualityDataResponseDto> result = airQualityService.getTopCitiesWithDataInDepartment("34", 5);

            assertTrue(result.isEmpty());
            verify(measurementRepository, never()).findTopByStation_City_InseeCodeOrderByMeasuredAtDesc(any());
        }

        @Test
        @DisplayName("should skip cities with null areaCode when INSEE has no measurements")
        void shouldSkipCitiesWithNullAreaCodeWhenInseeHasNoMeasurements() {
            city.setAreaCode(null);

            when(cityRepository.findAll()).thenReturn(List.of(city));
            when(measurementRepository.findTopByStation_City_InseeCodeOrderByMeasuredAtDesc("34172"))
                    .thenReturn(Optional.empty());

            List<AirQualityDataResponseDto> result = airQualityService.getTopCitiesWithDataInDepartment("34", 5);

            assertTrue(result.isEmpty());
        }
    }
}
