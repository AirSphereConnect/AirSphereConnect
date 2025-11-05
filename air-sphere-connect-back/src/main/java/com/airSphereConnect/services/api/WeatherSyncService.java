package com.airSphereConnect.services.api;

import com.airSphereConnect.dtos.response.ApiWeatherResponseDto;
import com.airSphereConnect.entities.City;
import com.airSphereConnect.entities.WeatherMeasurement;
import com.airSphereConnect.repositories.CityRepository;
import com.airSphereConnect.repositories.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import static com.airSphereConnect.configuration.WebClientConfig.WEATHER_API_BASEURL;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Service
@Transactional
public class WeatherSyncService implements DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherSyncService.class);

    @Value("${weather.api.key}")
    private String apiKey;

    private final CityRepository cityRepository;
    private final WeatherRepository weatherRepository;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    private final boolean enabled = true;
    private final Duration syncInterval = Duration.ofHours(1);
    private LocalDateTime lastSync = null;
    private int consecutiveErrors = 0;

    public WeatherSyncService(CityRepository cityRepository,
                              WeatherRepository weatherRepository,
                              ObjectMapper objectMapper,
                              WebClient weatherApiWebClient) {
        this.cityRepository = cityRepository;
        this.weatherRepository = weatherRepository;
        this.objectMapper = objectMapper;
        this.webClient = weatherApiWebClient;
    }

    @Override
    public String getServiceName() {
        return "WEATHER";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Duration getSyncInterval() {
        return syncInterval;
    }

    @Override
    public LocalDateTime getLastSync() {
        return lastSync;
    }

    @Override
    public int getConsecutiveErrors() {
        return consecutiveErrors;
    }

    @Override
    public void syncData() {
        if (!enabled) {
            logger.info("Service {} désactivé, aucun appel de synchronisation.", getServiceName());
            return;
        }

        try {
            fetchAndStoreWeatherForAllCities();
            lastSync = LocalDateTime.now();
            consecutiveErrors = 0;
            logger.info("Service {} : synchronisation réussie", getServiceName());
        } catch (Exception e) {
            consecutiveErrors++;
            logger.error("Service {} : erreur lors de la synchronisation : {}", getServiceName(), e.getMessage(), e);
        }
    }

    public void fetchAndStoreWeatherForAllCities() {

        List<City> cities = cityRepository.findAll();

        List<WeatherMeasurement> weatherList = Flux.fromIterable(cities)
                .flatMap(city -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("lat", city.getLatitude())
                                .queryParam("lon", city.getLongitude())
                                .queryParam("appid", apiKey)
                                .queryParam("units", "metric")
                                .build())
                        .retrieve()
                        .bodyToMono(ApiWeatherResponseDto.class)
                        .map(response -> {
                            if (response == null) {
                                logger.warn("Réponse météo nulle pour la ville {}", city.getName());
                                return null;
                            }
                            WeatherMeasurement weather = new WeatherMeasurement();
                            weather.setCity(city);
                            if (response.weatherMainDto() != null) {
                                weather.setTemperature(round(response.weatherMainDto().temp(), 1));
                                weather.setPressure(round(response.weatherMainDto().pressure(), 0));
                                weather.setHumidity(round(response.weatherMainDto().humidity(), 0));
                            }
                            if (response.weatherWindDto() != null) {
                                weather.setWindDirection(round(response.weatherWindDto().deg(), 0));
                                // Convert wind speed from m/s to km/h (multiply by 3.6)
                                Double windSpeedMs = response.weatherWindDto().speed();
                                weather.setWindSpeed(windSpeedMs != null ? round(windSpeedMs * 3.6, 2) : null);
                            }
                            if (response.weatherDescriptionDto() != null && response.weatherDescriptionDto().length > 0) {
                                try {
                                    String jsonMessage = objectMapper.writeValueAsString(response.weatherDescriptionDto());
                                    weather.setMessage(jsonMessage);
                                } catch (JsonProcessingException e) {
                                    logger.error("Erreur sérialisation message météo: {}", e.getMessage());
                                    weather.setMessage(null);
                                }
                            }
                            weather.setSource(WEATHER_API_BASEURL);
                            boolean hasAlert = response.weatherAlertDto() != null && response.weatherAlertDto().length > 0;
                            weather.setAlert(hasAlert);
                            if (hasAlert) {
                                try {
                                    String alertJson = objectMapper.writeValueAsString(response.weatherAlertDto());
                                    weather.setAlertMessage(alertJson);
                                } catch (JsonProcessingException e) {
                                    logger.error("Erreur sérialisation alert message météo: {}", e.getMessage());
                                    weather.setAlertMessage(null);
                                }
                            } else {
                                weather.setAlertMessage(null);
                            }
                            weather.setMeasuredAt(LocalDateTime.now());
                            return weather;
                        })
                        .onErrorResume(e -> {
                            logger.error("Erreur météo pour {} : {}", city.getName(), e.getMessage());
                            return Mono.empty();
                        })
                )
                .filter(weather -> weather != null)
                .collectList()
                .block();

        if (weatherList != null && !weatherList.isEmpty()) {
            LocalDateTime currentHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            LocalDateTime nextHour = currentHour.plusHours(1);

            List<WeatherMeasurement> toSave = weatherList.stream()
                .filter(weather -> {
                    boolean exists = weatherRepository.existsByCityAndMeasuredAtBetween(
                        weather.getCity(),
                        currentHour,
                        nextHour
                    );
                    return !exists;
                })
                .toList();

            if (!toSave.isEmpty()) {
                weatherRepository.saveAll(toSave);
                logger.info("Synchronisation météo : {} mesures insérées ({} doublons ignorés).",
                    toSave.size(), weatherList.size() - toSave.size());
            } else {
                logger.info("Synchronisation météo : aucune nouvelle mesure (toutes existent déjà).");
            }
        } else {
            logger.warn("Aucune mesure météo à insérer.");
        }
    }

    private Double round(Double value, int decimals) {
        if (value == null) return null;
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }
}
