package com.airSphereConnect.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebClientConfig {

    public static final String WEATHER_API_BASEURL = "openweathermap.org";
    public static final String POP_API_BASEURL = "geo.api.gouv.fr";
    public static final String ATMO_API_BASE_URL = "services9.arcgis.com";

    ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024))
            .build();

    @Bean
    public WebClient populationApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://" + POP_API_BASEURL)
                .build();
    }

    @Bean
    public WebClient weatherApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api." + WEATHER_API_BASEURL + "/data/2.5/weather")
                .build();
    }

    @Bean
    public WebClient atmoApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://" + ATMO_API_BASE_URL + "/7Sr9Ek9c1QTKmbwr/arcgis/rest/services")
                .exchangeStrategies(strategies)
                .defaultHeader("User-Agent", "AirSphereConnect/1.0")
                .defaultHeader("Accept", "application/json")
                .build();
    }

}
