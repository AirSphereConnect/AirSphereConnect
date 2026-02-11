package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.ExportDto;
import com.airSphereConnect.entities.*;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.AirQualityIndexRepository;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.WeatherRepository;
import com.airSphereConnect.utils.CsvExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExportServiceImpl Test Suite")
class ExportServiceImplTest {

    @Mock
    private AirQualityStationRepository airQualityStationRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private AirQualityIndexRepository airQualityIndexRepository;

    @Mock
    private CsvExporter csvExporter;

    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp() {
        exportService = new ExportServiceImpl(
                airQualityStationRepository,
                cityRepository,
                weatherRepository,
                airQualityIndexRepository,
                csvExporter
        );
    }

    @Nested
    @DisplayName("getCompleteDataByCity tests")
    class GetCompleteDataByCityTests {

        @Test
        @DisplayName("should throw exception when city not found")
        void shouldThrowExceptionWhenCityNotFound() {
            when(cityRepository.findByInseeCode("99999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> exportService.getCompleteDataByCity("99999", null, null, "weather"))
                    .isInstanceOf(GlobalException.ResourceNotFoundException.class)
                    .hasMessageContaining("City with INSEE code 99999 not found");
        }

        @Test
        @DisplayName("should return weather data only when type is weather")
        void shouldReturnWeatherDataOnly() {
            City city = createCity();
            WeatherMeasurement weather = createWeatherMeasurement(city);

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(weatherRepository.findByCityId(city.getId())).thenReturn(List.of(weather));

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "weather");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).nomVille()).isEqualTo("Montpellier");
            assertThat(result.get(0).temperature()).contains("25");
            assertThat(result.get(0).stationId()).isEqualTo("/");
        }

        @Test
        @DisplayName("should filter weather data by date range")
        void shouldFilterWeatherDataByDateRange() {
            City city = createCity();
            WeatherMeasurement weather1 = createWeatherMeasurement(city);
            weather1.setMeasuredAt(LocalDateTime.of(2024, 1, 15, 10, 0));

            WeatherMeasurement weather2 = createWeatherMeasurement(city);
            weather2.setMeasuredAt(LocalDateTime.of(2024, 1, 20, 10, 0));

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(weatherRepository.findByCityId(city.getId())).thenReturn(List.of(weather1, weather2));

            LocalDate dateDebut = LocalDate.of(2024, 1, 18);
            LocalDate dateFin = LocalDate.of(2024, 1, 25);

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", dateDebut, dateFin, "weather");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return air quality data when type is air-quality")
        void shouldReturnAirQualityData() {
            City city = createCityWithStation();

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(airQualityIndexRepository.findByAreaCodeAndMeasuredAt(any(), any()))
                    .thenReturn(Optional.empty());

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "air-quality");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).pm25()).contains("15");
        }

        @Test
        @DisplayName("should use nearest station when city has no station")
        void shouldUseNearestStationWhenCityHasNoStation() {
            City city = createCity();
            city.setAirQualityStations(new ArrayList<>());

            AirQualityStation nearestStation = createStationWithMeasurement();
            nearestStation.setInseeCode("34172");

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(airQualityStationRepository.findAll()).thenReturn(List.of(nearestStation));
            when(airQualityIndexRepository.findByAreaCodeAndMeasuredAt(any(), any()))
                    .thenReturn(Optional.empty());

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "air-quality");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should return empty list when no station found for air-quality")
        void shouldReturnEmptyListWhenNoStationFound() {
            City city = createCity();
            city.setAirQualityStations(new ArrayList<>());
            city.setInseeCode(null);
            city.setAreaCode(null);

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "air-quality");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return combined data when type is default")
        void shouldReturnCombinedData() {
            City city = createCityWithStation();
            WeatherMeasurement weather = createWeatherMeasurement(city);
            weather.setMeasuredAt(LocalDateTime.of(2024, 1, 15, 10, 0));

            // Set air quality measurement date to match weather
            city.getAirQualityStations().get(0).getMeasurements().get(0)
                    .setMeasuredAt(LocalDateTime.of(2024, 1, 15, 12, 0));

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(weatherRepository.findByCityId(city.getId())).thenReturn(List.of(weather));
            when(airQualityIndexRepository.findByAreaCodeAndMeasuredAt(any(), any()))
                    .thenReturn(Optional.empty());

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "combined");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).temperature()).contains("25");
            assertThat(result.get(0).pm25()).contains("15");
        }

        @Test
        @DisplayName("should return weather only when no station for combined export")
        void shouldReturnWeatherOnlyWhenNoStationForCombined() {
            City city = createCity();
            city.setAirQualityStations(new ArrayList<>());
            city.setInseeCode(null);
            city.setAreaCode(null);

            WeatherMeasurement weather = createWeatherMeasurement(city);

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(weatherRepository.findByCityId(city.getId())).thenReturn(List.of(weather));

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "combined");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).temperature()).contains("25");
            assertThat(result.get(0).stationId()).isEqualTo("/");
        }

        @Test
        @DisplayName("should find station by areaCode when insee not found")
        void shouldFindStationByAreaCode() {
            City city = createCity();
            city.setAirQualityStations(new ArrayList<>());
            city.setInseeCode("99999"); // Different from station

            AirQualityStation nearestStation = createStationWithMeasurement();
            nearestStation.setInseeCode("34000"); // Different INSEE
            nearestStation.setAreaCode("34"); // Same area code

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(airQualityStationRepository.findAll()).thenReturn(List.of(nearestStation));
            when(airQualityIndexRepository.findByAreaCodeAndMeasuredAt(any(), any()))
                    .thenReturn(Optional.empty());

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "air-quality");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should include air quality index when available")
        void shouldIncludeAirQualityIndex() {
            City city = createCityWithStation();

            AirQualityIndex index = new AirQualityIndex();
            index.setQualityIndex(3);
            index.setQualityLabel("Moyen");

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(airQualityIndexRepository.findByAreaCodeAndMeasuredAt(any(), any()))
                    .thenReturn(Optional.of(index));

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "air-quality");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).qualiteIndex()).isEqualTo("3");
            assertThat(result.get(0).qualiteLabel()).isEqualTo("Moyen");
        }

        @Test
        @DisplayName("should handle null message in weather")
        void shouldHandleNullMessageInWeather() {
            City city = createCity();
            WeatherMeasurement weather = createWeatherMeasurement(city);
            weather.setMessage(null);

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(weatherRepository.findByCityId(city.getId())).thenReturn(List.of(weather));

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "weather");

            assertThat(result.get(0).message()).isEqualTo("/");
        }

        @Test
        @DisplayName("should handle null unit in air quality")
        void shouldHandleNullUnitInAirQuality() {
            City city = createCityWithStation();
            city.getAirQualityStations().get(0).getMeasurements().get(0).setUnit(null);

            when(cityRepository.findByInseeCode("34172")).thenReturn(Optional.of(city));
            when(airQualityIndexRepository.findByAreaCodeAndMeasuredAt(any(), any()))
                    .thenReturn(Optional.empty());

            List<ExportDto> result = exportService.getCompleteDataByCity("34172", null, null, "air-quality");

            assertThat(result.get(0).unite()).isEqualTo("/");
        }
    }

    // Helper methods
    private City createCity() {
        City city = new City();
        city.setId(1L);
        city.setName("Montpellier");
        city.setInseeCode("34172");
        city.setAreaCode("34");
        city.setLatitude(43.6119);
        city.setLongitude(3.8772);
        city.setPopulation(290000);
        city.setAirQualityStations(new ArrayList<>());
        return city;
    }

    private City createCityWithStation() {
        City city = createCity();
        AirQualityStation station = createStationWithMeasurement();
        station.setCity(city);
        city.setAirQualityStations(new ArrayList<>(List.of(station)));
        return city;
    }

    private AirQualityStation createStationWithMeasurement() {
        AirQualityStation station = new AirQualityStation();
        station.setId(1L);
        station.setCode("FR31002");
        station.setName("Montpellier Centre");
        station.setAreaCode("34");

        AirQualityMeasurement measurement = new AirQualityMeasurement();
        measurement.setStation(station);
        measurement.setMeasuredAt(LocalDateTime.now());
        measurement.setPm25(15.5);
        measurement.setPm10(25.0);
        measurement.setNo2(30.0);
        measurement.setO3(45.0);
        measurement.setUnit("µg/m³");

        station.setMeasurements(new ArrayList<>(List.of(measurement)));
        return station;
    }

    private WeatherMeasurement createWeatherMeasurement(City city) {
        WeatherMeasurement weather = new WeatherMeasurement();
        weather.setCity(city);
        weather.setMeasuredAt(LocalDateTime.now());
        weather.setTemperature(25.5);
        weather.setHumidity(60.0);
        weather.setPressure(1013.0);
        weather.setWindSpeed(15.0);
        weather.setWindDirection(180.0);
        weather.setMessage("Sunny");
        return weather;
    }
}
