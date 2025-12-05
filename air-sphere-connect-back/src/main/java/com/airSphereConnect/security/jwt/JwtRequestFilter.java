package com.airSphereConnect.security.jwt;

import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Filtre JWT moderne et robuste pour Spring Security 6+ / Java 21.
 * Skip automatiquement les endpoints publics (.permitAll()) et OPTIONS CORS.
 * Gère proprement les erreurs sans bloquer la chaîne de filtres.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    //Solution temporaire pour éviter les boucles infinies
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/api/login", "/api/profile", "/api/refresh-token", "/api/guest-token",
            "/api/users/check", "/api/cities/search-name", "/api/users/signup",
            "/actuator/health", "/actuator/info", "/swagger-ui", "/v3/api-docs"
    );

    private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    private final JwtServiceImpl jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtRequestFilter(JwtServiceImpl jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String requestPath = normalizePath(request.getRequestURI());
        final String method = request.getMethod();

        logger.debug("JWT Filter → Path: {} | Method: {}", requestPath, method);
        //Solution temporaire pour éviter les boucles infinies
        // 1. Skip endpoints publics (.permitAll()) et CORS preflight
        if (shouldSkipFilter(requestPath, method)) {
            logger.debug("Skip JWT filter pour endpoint public: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        // 2. Extraire et valider JWT
        final String jwt = extractJwt(request);
        if (jwt == null || jwt.trim().isEmpty()) {
            logger.debug("Pas de JWT → requête anonyme");
            chain.doFilter(request, response);
            return;
        }

        try {
            // 3. Authentifier si valide
            if (authenticateValidToken(request, jwt)) {
                logger.debug("JWT valide → utilisateur authentifié");
            }
        } catch (Exception e) {
            logger.warn("Erreur validation JWT '{}': {}", requestPath, e.getMessage());
            SecurityContextHolder.clearContext();
            // Ne pas set 401 ici → laisser Spring Security gérer
        } finally {
            // 4. TOUJOURS continuer la chaîne
            chain.doFilter(request, response);
        }
    }

    /**
     * Normalise le path pour comparaison (supprime le context path).
     */
    private String normalizePath(String path) {
        final String contextPath = getServletContext().getContextPath();
        return contextPath.isEmpty() ? path : path.substring(contextPath.length());
    }

    /**
     * Détermine si le filtre doit skipper cette requête.
     */
    private boolean shouldSkipFilter(String path, String method) {
        return "OPTIONS".equals(method) || PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Extrait le JWT depuis le cookie ACCESS_TOKEN.
     */
    private String extractJwt(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return java.util.Arrays.stream(request.getCookies())
                .filter(cookie -> ACCESS_TOKEN_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> value != null && !value.trim().isEmpty())
                .findFirst()
                .orElse(null);
    }

    /**
     * Authentifie un token JWT valide dans SecurityContext.
     * @return true si authentification réussie
     */
    private boolean authenticateValidToken(HttpServletRequest request, String jwt) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return false; // Déjà authentifié
        }

        final String username = jwtService.extractUsername(jwt);
        if (username == null) {
            return false;
        }

        final UserDetails userDetails = loadUserDetails(username);
        if (!jwtService.validateToken(jwt, userDetails)) {
            return false;
        }

        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return true;
    }

    /**
     * Charge UserDetails (guest ou DB).
     */
    private UserDetails loadUserDetails(String username) {
        if ("guest".equals(username)) {
            final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
            return org.springframework.security.core.userdetails.User
                    .withUsername("guest")
                    .password("")
                    .authorities(authorities)
                    .build();
        }
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Fallback pour les patterns antMatchers (rarement utilisé)
        return shouldSkipFilter(normalizePath(request.getRequestURI()), request.getMethod());
    }
}
