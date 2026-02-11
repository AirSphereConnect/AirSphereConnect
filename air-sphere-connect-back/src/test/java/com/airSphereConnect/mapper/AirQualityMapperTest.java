package com.airSphereConnect.mapper;

import com.airSphereConnect.dtos.response.AirQualityIndexResponseDto;
import com.airSphereConnect.dtos.response.AirQualityMeasurementResponseDto;
import com.airSphereConnect.dtos.response.AirQualityStationResponseDto;
import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.entities.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AirQualityMapper Test Suite")
class AirQualityMapperTest {

    private AirQualityMapper mapper;
    private City city;
    private AirQualityStation station;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        mapper = new AirQualityMapper();
        now = LocalDateTime.now();

        city = new City();
        city.setId(1L);
        city.setName("Montpellier");

        station = new AirQualityStation();
        station.setId(1L);
        station.setName("Station Montpellier");
        station.setCode("ATMO-34");
        station.setAreaCode("243400017");
        station.setCity(city);
    }

    @Nested
    @DisplayName("toDto(AirQualityStation)")
    class StationToDtoTests {

        @Test
        @DisplayName("should return null for null station")
        void shouldReturnNullForNullStation() {
            assertNull(mapper.toDto((AirQualityStation) null));
        }

        @Test
        @DisplayName("should map station to dto correctly")
        void shouldMapStationToDto() {
            AirQualityStationResponseDto dto = mapper.toDto(station);

            assertNotNull(dto);
            assertEquals(1L, dto.id());
            assertEquals("Station Montpellier", dto.name());
            assertEquals("ATMO-34", dto.code());
            assertEquals("243400017", dto.areaCode());
            assertEquals("Montpellier", dto.city());
        }

        @Test
        @DisplayName("should handle station with null city")
        void shouldHandleNullCity() {
            station.setCity(null);
            AirQualityStationResponseDto dto = mapper.toDto(station);

            assertNotNull(dto);
            assertNull(dto.city());
        }
    }

    @Nested
    @DisplayName("toDto(AirQualityMeasurement)")
    class MeasurementToDtoTests {

        private AirQualityMeasurement measurement;

        @BeforeEach
        void setUp() {
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
        }

        @Test
        @DisplayName("should return null for null measurement")
        void shouldReturnNullForNullMeasurement() {
            assertNull(mapper.toDto((AirQualityMeasurement) null));
        }

        @Test
        @DisplayName("should map measurement with default data source")
        void shouldMapMeasurementWithDefaultSource() {
            AirQualityMeasurementResponseDto dto = mapper.toDto(measurement);

            assertNotNull(dto);
            assertEquals(1L, dto.id());
            assertEquals(25.0, dto.pm10());
            assertEquals(15.0, dto.pm25());
            assertEquals(30.0, dto.no2());
            assertEquals(50.0, dto.o3());
            assertEquals(5.0, dto.so2());
            assertEquals("µg/m³", dto.unit());
            assertEquals(now, dto.measuredAt());
            assertEquals("Station Montpellier", dto.station());
            assertEquals("exact", dto.dataSource());
            assertNull(dto.sourceCities());
        }

        @Test
        @DisplayName("should handle measurement with null station")
        void shouldHandleMeasurementWithNullStation() {
            measurement.setStation(null);
            AirQualityMeasurementResponseDto dto = mapper.toDto(measurement);

            assertNotNull(dto);
            assertNull(dto.station());
        }
    }

    @Nested
    @DisplayName("toDto(AirQualityMeasurement, dataSource, sourceCities)")
    class MeasurementWithSourceToDtoTests {

        private AirQualityMeasurement measurement;

        @BeforeEach
        void setUp() {
            measurement = new AirQualityMeasurement();
            measurement.setId(2L);
            measurement.setPm10(30.0);
            measurement.setStation(station);
            measurement.setMeasuredAt(now);
        }

        @Test
        @DisplayName("should return null for null measurement")
        void shouldReturnNullForNull() {
            assertNull(mapper.toDto(null, "department", "Nîmes, Béziers"));
        }

        @Test
        @DisplayName("should map with custom data source and source cities")
        void shouldMapWithCustomSource() {
            AirQualityMeasurementResponseDto dto = mapper.toDto(measurement, "department", "Nîmes, Béziers");

            assertNotNull(dto);
            assertEquals(2L, dto.id());
            assertEquals("department", dto.dataSource());
            assertEquals("Nîmes, Béziers", dto.sourceCities());
        }
    }

    @Nested
    @DisplayName("toDto(AirQualityIndex, alertMessage)")
    class IndexToDtoTests {

        private AirQualityIndex index;

        @BeforeEach
        void setUp() {
            index = new AirQualityIndex();
            index.setId(1L);
            index.setQualityIndex(3);
            index.setQualityLabel("Moyen");
            index.setQualityColor("#FFFF00");
            index.setMeasuredAt(now);
            index.setAreaCode("243400017");
            index.setAreaName("Montpellier Méditerranée");
            index.setSource("ATMO Occitanie");
        }

        @Test
        @DisplayName("should return null for null index")
        void shouldReturnNullForNullIndex() {
            assertNull(mapper.toDto((AirQualityIndex) null, "alert"));
        }

        @Test
        @DisplayName("should map index with alert message")
        void shouldMapIndexWithAlert() {
            AirQualityIndexResponseDto dto = mapper.toDto(index, "Qualité moyenne");

            assertNotNull(dto);
            assertEquals(1L, dto.id());
            assertEquals(3, dto.qualityIndex());
            assertEquals("Moyen", dto.qualityLabel());
            assertEquals("#FFFF00", dto.qualityColor());
            assertEquals(now, dto.measuredAt());
            assertEquals("243400017", dto.areaCode());
            assertEquals("Montpellier Méditerranée", dto.areaName());
            assertEquals("ATMO Occitanie", dto.source());
            assertEquals("Qualité moyenne", dto.alertMessage());
        }

        @Test
        @DisplayName("should map index with null alert message")
        void shouldMapIndexWithNullAlert() {
            AirQualityIndexResponseDto dto = mapper.toDto(index, null);

            assertNotNull(dto);
            assertNull(dto.alertMessage());
        }
    }
}
