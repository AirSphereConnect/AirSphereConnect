package com.airSphereConnect.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    public static final String WEATHER_API_BASEURL = "openweathermap.org";

    @Bean
    public WebClient populationApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://geo.api.gouv.fr")
                .build();
    }

    @Bean
    public WebClient weatherApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api." + WEATHER_API_BASEURL + "/data/2.5/weather")
                .build();
    }
}
