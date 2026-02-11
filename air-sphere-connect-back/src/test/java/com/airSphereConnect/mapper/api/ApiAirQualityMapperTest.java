package com.airSphereConnect.mapper.api;

import com.airSphereConnect.dtos.response.AirQualityDailyMeasureResponseDto;
import com.airSphereConnect.dtos.response.AirQualityIndexMeasureResponseDto;
import com.airSphereConnect.entities.AirQualityIndex;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiAirQualityMapper Test Suite")
class ApiAirQualityMapperTest {

    private ApiAirQualityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ApiAirQualityMapper();
    }

    @Nested
    @DisplayName("toEntity(AirQualityDailyMeasureResponseDto) tests - Station")
    class ToStationEntityTests {

        @Test
        @DisplayName("should return null when dto is null")
        void shouldReturnNullWhenDtoIsNull() {
            AirQualityStation result = mapper.toEntity((AirQualityDailyMeasureResponseDto) null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map dto to station entity")
        void shouldMapDtoToStationEntity() {
            AirQualityDailyMeasureResponseDto dto = new AirQualityDailyMeasureResponseDto(
                    34172,                    // inseeCode (Integer)
                    "Montpellier Centre",     // nomStation
                    "FR31002",                // codeStation
                    "PM10",                   // polluantName
                    25.0,                     // polluantValue
                    "µg/m³",                  // polluantUnit
                    1704067200000L,           // dateDebutTimestamp
                    null                      // measuredAt
            );

            AirQualityStation result = mapper.toEntity(dto);

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo("FR31002");
            assertThat(result.getName()).isEqualTo("Montpellier Centre");
        }
    }

    @Nested
    @DisplayName("toEntity(AirQualityDailyMeasureResponseDto, AirQualityStation) tests - Measurement")
    class ToMeasurementEntityTests {

        @Test
        @DisplayName("should return null when dto is null")
        void shouldReturnNullWhenDtoIsNull() {
            AirQualityStation station = new AirQualityStation();

            AirQualityMeasurement result = mapper.toEntity((AirQualityDailyMeasureResponseDto) null, station);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map PM10 correctly")
        void shouldMapPM10Correctly() {
            AirQualityStation station = new AirQualityStation();
            AirQualityDailyMeasureResponseDto dto = createDto("PM10", 25.0, "µg/m³");

            AirQualityMeasurement result = mapper.toEntity(dto, station);

            assertThat(result).isNotNull();
            assertThat(result.getPm10()).isEqualTo(25.0);
            assertThat(result.getStation()).isEqualTo(station);
            assertThat(result.getUnit()).isEqualTo("µg/m³");
        }

        @Test
        @DisplayName("should map PM25 correctly")
        void shouldMapPM25Correctly() {
            AirQualityStation station = new AirQualityStation();
            AirQualityDailyMeasureResponseDto dto = createDto("PM25", 15.0, "µg/m³");

            AirQualityMeasurement result = mapper.toEntity(dto, station);

            assertThat(result).isNotNull();
            assertThat(result.getPm25()).isEqualTo(15.0);
        }

        @Test
        @DisplayName("should map NO2 correctly")
        void shouldMapNO2Correctly() {
            AirQualityStation station = new AirQualityStation();
            AirQualityDailyMeasureResponseDto dto = createDto("NO2", 30.0, "µg/m³");

            AirQualityMeasurement result = mapper.toEntity(dto, station);

            assertThat(result).isNotNull();
            assertThat(result.getNo2()).isEqualTo(30.0);
        }

        @Test
        @DisplayName("should map O3 correctly")
        void shouldMapO3Correctly() {
            AirQualityStation station = new AirQualityStation();
            AirQualityDailyMeasureResponseDto dto = createDto("O3", 45.0, "µg/m³");

            AirQualityMeasurement result = mapper.toEntity(dto, station);

            assertThat(result).isNotNull();
            assertThat(result.getO3()).isEqualTo(45.0);
        }

        @Test
        @DisplayName("should map SO2 correctly")
        void shouldMapSO2Correctly() {
            AirQualityStation station = new AirQualityStation();
            AirQualityDailyMeasureResponseDto dto = createDto("SO2", 10.0, "µg/m³");

            AirQualityMeasurement result = mapper.toEntity(dto, station);

            assertThat(result).isNotNull();
            assertThat(result.getSo2()).isEqualTo(10.0);
        }

        @Test
        @DisplayName("should handle unknown pollutant")
        void shouldHandleUnknownPollutant() {
            AirQualityStation station = new AirQualityStation();
            AirQualityDailyMeasureResponseDto dto = createDto("UNKNOWN", 100.0, "µg/m³");

            AirQualityMeasurement result = mapper.toEntity(dto, station);

            assertThat(result).isNotNull();
            assertThat(result.getPm10()).isNull();
            assertThat(result.getPm25()).isNull();
            assertThat(result.getNo2()).isNull();
            assertThat(result.getO3()).isNull();
            assertThat(result.getSo2()).isNull();
        }

        @Test
        @DisplayName("should use default unit when null")
        void shouldUseDefaultUnitWhenNull() {
            AirQualityStation station = new AirQualityStation();
            AirQualityDailyMeasureResponseDto dto = createDto("PM10", 25.0, null);

            AirQualityMeasurement result = mapper.toEntity(dto, station);

            assertThat(result.getUnit()).isEqualTo("µg/m³");
        }
    }

    @Nested
    @DisplayName("toEntity(AirQualityIndexMeasureResponseDto) tests")
    class ToIndexEntityTests {

        @Test
        @DisplayName("should return null when dto is null")
        void shouldReturnNullWhenDtoIsNull() {
            AirQualityIndex result = mapper.toEntity((AirQualityIndexMeasureResponseDto) null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map dto to index entity")
        void shouldMapDtoToIndexEntity() {
            AirQualityIndexMeasureResponseDto dto = new AirQualityIndexMeasureResponseDto(
                    "3",
                    "Moyen",
                    "#FFC107",
                    "ATMO",
                    "34",
                    "Hérault",
                    1704067200000L
            );

            AirQualityIndex result = mapper.toEntity(dto);

            assertThat(result).isNotNull();
            assertThat(result.getQualityIndex()).isEqualTo(3);
            assertThat(result.getQualityLabel()).isEqualTo("Moyen");
            assertThat(result.getQualityColor()).isEqualTo("#FFC107");
            assertThat(result.getSource()).isEqualTo("ATMO");
            assertThat(result.getAreaCode()).isEqualTo("34");
            assertThat(result.getAreaName()).isEqualTo("Hérault");
            assertThat(result.getMeasuredAt()).isNotNull();
        }
    }

    // Helper method
    private AirQualityDailyMeasureResponseDto createDto(String pollutant, Double value, String unit) {
        return new AirQualityDailyMeasureResponseDto(
                34172,                    // inseeCode (Integer)
                "Montpellier Centre",     // nomStation
                "FR31002",                // codeStation
                pollutant,                // polluantName
                value,                    // polluantValue
                unit,                     // polluantUnit
                1704067200000L,           // dateDebutTimestamp
                null                      // measuredAt
        );
    }
}
