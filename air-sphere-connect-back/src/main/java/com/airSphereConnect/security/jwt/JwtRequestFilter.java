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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtServiceImpl;
    private final CustomUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    public JwtRequestFilter(JwtServiceImpl jwtServiceImpl,
                            CustomUserDetailsService userDetailsService) {
        this.jwtServiceImpl = jwtServiceImpl;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("Filtrage JWT pour l'URL : {}", request.getRequestURI());

        String jwt = extractJwtFromCookies(request);

        if (jwt == null || jwt.isBlank()) {
            logger.debug("Aucun token JWT trouvé ou token vide - requête non authentifiée");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtServiceImpl.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = "guest".equals(username)
                        ? createGuestUserDetails()
                        : userDetailsService.loadUserByUsername(username);

                if (jwtServiceImpl.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("Utilisateur authentifié avec succès : {}", username);
                } else {
                    logger.warn("Token JWT invalide pour {}", username);
                    sendUnauthorizedResponse(response, "Token JWT invalide ou expiré");
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la validation JWT : ", e);
            sendUnauthorizedResponse(response, "Erreur de validation du token JWT");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if (ACCESS_TOKEN.equals(cookie.getName())) {
                logger.debug("Token JWT trouvé dans cookie");
                return cookie.getValue();
            }
        }
        return null;
    }

    private UserDetails createGuestUserDetails() {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
        logger.debug("Utilisateur invité créé sans base : guest");
        return new User("guest", "", authorities);
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String json = String.format("{\"error\": \"%s\"}", message);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}

