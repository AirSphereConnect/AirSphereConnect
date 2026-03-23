package com.airsphereconnect.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI / Swagger pour la documentation de l'API AirSphere Connect.
 *
 * La documentation sera accessible à l'URL : http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI airSphereConnectAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AirSphere Connect API")
                        .version("1.0.0")
                        .description("""
                                API REST pour AirSphere Connect - Plateforme de surveillance de la qualité de l'air et des données météorologiques.

                                ## Fonctionnalités principales :
                                - 🌍 Gestion des villes et données géographiques
                                - 🍃 Surveillance de la qualité de l'air (indices, polluants)
                                - ☁️ Données météorologiques en temps réel
                                - 📊 Historique et exports (CSV, PDF)
                                - 👤 Gestion des utilisateurs et authentification
                                - 💬 Forum de discussion

                                ## Authentification :
                                La plupart des endpoints nécessitent une authentification JWT.
                                Utilisez le endpoint `/api/auth/login` pour obtenir un token.
                                """)
                        .contact(new Contact()
                                .name("Équipe AirSphere Connect")
                                .email("support@airsphereconnect.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://airsphereconnect.sandrinealcazar.ovh")
                                .description("Serveur de production")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez le token JWT reçu lors de la connexion")));
    }
}
