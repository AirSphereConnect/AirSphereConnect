package com.airSphereConnect.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient populationApiWebClient() {
        return WebClient.builder()
                .baseUrl("https://geo.api.gouv.fr")
                .build();
    }
}
