package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiWeatherResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.WeatherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import static com.airSphereConnect.configuration.WebClientConfig.WEATHER_API_BASEURL;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class WeatherSyncService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherSyncService.class);

    @Value("${weather.api.key}")
    private String apiKey;

    private final CityRepository cityRepository;
    private final WeatherRepository weatherRepository;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public WeatherSyncService(CityRepository cityRepository,
                              WeatherRepository weatherRepository,
                              ObjectMapper objectMapper,
                              WebClient weatherApiWebClient) {
        this.cityRepository = cityRepository;
        this.weatherRepository = weatherRepository;
        this.objectMapper = objectMapper;
        this.webClient = weatherApiWebClient;
    }

    public void fetchAndStoreWeatherForAllCities() {

        List<City> cities = cityRepository.findAll();

        for (City city : cities) {
            try {
                ApiWeatherResponseDto response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("lat", city.getLatitude())
                                .queryParam("lon", city.getLongitude())
                                .queryParam("appid", apiKey)
                                .queryParam("units", "metric")
                                .build())
                        .retrieve()
                        .bodyToMono(ApiWeatherResponseDto.class)
                        .block();

                if (response == null) {
                    logger.warn("Réponse météo nulle pour la ville {}", city.getName());
                    continue;
                }

                WeatherMeasurement weather = new WeatherMeasurement();
                weather.setCity(city);

                if (response.weatherMainDto() != null) {
                    weather.setTemperature(response.weatherMainDto().temp());
                    weather.setPressure(response.weatherMainDto().pressure());
                    weather.setHumidity(response.weatherMainDto().humidity());
                }

                if (response.weatherWindDto() != null) {
                    weather.setWindDirection(response.weatherWindDto().deg());
                    weather.setWindSpeed(response.weatherWindDto().speed());
                }

                if (response.weatherDescriptionDto() != null && response.weatherDescriptionDto().length > 0) {

                    String jsonMessage = objectMapper.writeValueAsString(response.weatherDescriptionDto());
                    weather.setMessage(jsonMessage);
//                    logger.debug("Message JSON stocké : {}", jsonMessage);
                }
                weather.setSource(WEATHER_API_BASEURL);

                boolean hasAlert = response.weatherAlertDto() != null && response.weatherAlertDto().length > 0;
                weather.setAlert(hasAlert);

                if (hasAlert) {
                    String alertJson = objectMapper.writeValueAsString(Arrays.toString(response.weatherAlertDto()));
                    weather.setAlertMessage(alertJson);
                } else {
                    weather.setAlertMessage(null);
                }

                weather.setMeasuredAt(LocalDateTime.now());

                weatherRepository.save(weather);

            } catch (Exception e) {
                logger.error("Erreur météo pour {} : {}", city.getName(), e.getMessage());
            }
        }
    }
}
