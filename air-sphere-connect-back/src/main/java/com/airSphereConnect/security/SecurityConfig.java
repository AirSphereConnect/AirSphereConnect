package com.airSphereConnect.security;

import com.airSphereConnect.security.jwt.JwtRequestFilter;
import com.airSphereConnect.services.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println("{ \"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\" }");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // active CORS avec configuration source
                //TODO activer csrf pour la production (sinon problème aux attaques XSRF → récupération du cookie dans le header)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(handler -> handler.authenticationEntryPoint(unauthorizedHandler()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health",
                                "/actuator/info",
                                "/api/refresh-token",
                                "/api/guest-token",
                                "/api/login",
                                "/api/profile",
                                "api/logout",
                                "/api/users/check",
                                "/api/cities/search-name").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/users/signup").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // hasRole ajoute ROLE_ automatiquement
                        .requestMatchers("/api/home",
                                "/api/weather/**",
                                "/api/favorites/**",
                                "/api/air-quality/**",
                                "/api/forums/**", "/forum-posts/**", "/forum-rubrics/**", "/forum-threads/**",
                                "/api/cities/**",
                                "/api/address/**",
                                "/api/history/**",
                                "/api/regions/**",
                                "/api/alert/configurations/**")
                        .hasAnyRole("USER", "ADMIN", "GUEST")
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origine frontend Angular explicitement déclarée (pas de wildcard avec credentials)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers autorisés — * autorise tous les headers côté client
        configuration.setAllowedHeaders(List.of("*"));

        // Doit être true pour autoriser les cookies (dont les JWT HTTPOnly)
        configuration.setAllowCredentials(true);

        // Expose certains headers si besoin (exemple Authorization)
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Appliquer la configuration pour les endpoints API
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

}
