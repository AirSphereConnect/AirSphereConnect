package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.ExportDto;
import com.airSphereConnect.entities.AirQualityMeasurement;
import com.airSphereConnect.entities.AirQualityStation;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.AirQualityStationRepository;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.WeatherRepository;
import com.airSphereConnect.services.ExportService;
import com.airSphereConnect.utils.CsvExporter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExportServiceImpl implements ExportService {

    private final AirQualityStationRepository airQualityStationRepository;
    private final CityRepository cityRepository;
    private final WeatherRepository weatherRepository;

    public ExportServiceImpl(AirQualityStationRepository airQualityStationRepository, CityRepository cityRepository, WeatherRepository weatherRepository, CsvExporter csvExporter) {
        this.airQualityStationRepository = airQualityStationRepository;
        this.cityRepository = cityRepository;
        this.weatherRepository = weatherRepository;
    }

    @Override
    public List<ExportDto> getCompleteDataByCity(String inseeCode, LocalDate dateDebut, LocalDate dateFin) {

        // Récupérer la ville par son code INSEE
        City city =
                cityRepository.findByInseeCode(inseeCode).orElseThrow(() -> new GlobalException.ResourceNotFoundException("City with INSEE code " + inseeCode + " not found"));

        // Récupérer les mesures météo de la ville
        List<WeatherMeasurement> weatherMeasurements = weatherRepository.findByCityId(city.getId()).stream()
                .filter(wm -> (dateDebut == null || !wm.getMeasuredAt().toLocalDate().isBefore(dateDebut)) &&
                        (dateFin == null || !wm.getMeasuredAt().toLocalDate().isAfter(dateFin)))
                .toList();

        // TODO : récupérer par areaCode si pas de mesure par station avec inseeCode

        List<ExportDto> mergedData = new ArrayList<>();

        for (AirQualityStation station : city.getAirQualityStations()) {

            // Récupérer les mesures de qualité de l'air pour chaque station
            List<AirQualityMeasurement> airQualityMeasurements = station.getMeasurements();

            // Créer une map pour accéder rapidement à la dernière mesure de chaque date
            Map<LocalDate, AirQualityMeasurement> lastestMeasurementsByDate = airQualityMeasurements.stream()
                    .collect(Collectors.toMap(
                            aqm -> aqm.getMeasuredAt().toLocalDate(),
                            Function.identity(),
                            (existing, replacement) -> existing.getMeasuredAt().isAfter(replacement.getMeasuredAt()) ? existing : replacement
                    ));

            for (WeatherMeasurement wm : weatherMeasurements) {
                AirQualityMeasurement aqm = lastestMeasurementsByDate.get(wm.getMeasuredAt().toLocalDate());
                ExportDto dto = new ExportDto(

                        wm.getMeasuredAt().toLocalDate(),
                        city.getName(),
                        city.getLatitude(),
                        city.getLongitude(),
                        city.getPopulation(),
                        wm.getTemperature(),
                        wm.getHumidity(),
                        wm.getPressure(),
                        wm.getWindSpeed(),
                        wm.getWindDirection(),
                        wm.getMessage(),
                        station.getId(),
                        aqm.getPm25(),
                        aqm.getPm10(),
                        aqm.getNo2(),
                        aqm.getO3(),
                        aqm.getUnit(),
                        aqm.getStation().getCity().getAirQualityIndex().getQualityIndex(),
                        aqm.getStation().getCity().getAirQualityIndex().getQualityLabel()
                );

                mergedData.add(dto);
            }
        }
        return mergedData;
    }
}
