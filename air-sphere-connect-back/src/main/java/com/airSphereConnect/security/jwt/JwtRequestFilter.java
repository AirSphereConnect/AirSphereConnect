package com.airSphereConnect.security.jwt;

import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre JWT interceptant chaque requête HTTP pour extraire, valider
 * et établir l'authentification Spring Security si le token JWT est valide.
 *
 * Utilise le cookie "ACCESS_TOKEN" pour récupérer le JWT.
 * En cas d'erreur de validation ou absence de token, laisse la requête non authentifiée.
 * Les exceptions d'authentification JWT sont relancées pour être traitées globalement.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtServiceImpl;
    private final CustomUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    /**
     * Constructeur injectant les services nécessaires.
     *
     * @param jwtServiceImpl     service de gestion et validation des JWT
     * @param userDetailsService service pour charger les données utilisateur
     */
    public JwtRequestFilter(JwtServiceImpl jwtServiceImpl,
                            CustomUserDetailsService userDetailsService) {
        this.jwtServiceImpl = jwtServiceImpl;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filtre interceptant chaque requête HTTP entrante.
     * Tente d'extraire un JWT depuis les cookies, valide ce token,
     * puis établit le contexte d'authentification Spring Security.
     *
     * @param request     requête HTTP entrante
     * @param response    réponse HTTP sortante
     * @param filterChain chaîne de filtres servlet
     * @throws ServletException en cas d'erreur de traitement servlet
     * @throws IOException      en cas d'erreur d'entrée/sortie
     */
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
                    throw new GlobalException.JwtAuthenticationException("Token JWT invalide ou expiré");
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.debug("Utilisateur authentifié avec succès : {}", username);
            }
        } catch (GlobalException.JwtAuthenticationException ex) {
            logger.warn("JWT Authentication error: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
            // C'est l'advice qui va construire la response.
            throw ex;
        } catch (Exception e) {
            logger.error("Erreur lors de la validation JWT : ", e);
            SecurityContextHolder.clearContext();
            throw new GlobalException.JwtAuthenticationException("Erreur de validation du token JWT");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrait la valeur du token JWT depuis le cookie nommé ACCESS_TOKEN.
     *
     * @param request requête HTTP à analyser
     * @return le token JWT sous forme de chaîne, ou {@code null} si absent
     */
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

    /**
     * Crée un objet {@link UserDetails} représentant un utilisateur invité non authentifié (guest).
     *
     * @return un utilisateur avec rôle ROLE_GUEST et sans authentification réelle
     */
    private UserDetails createGuestUserDetails() {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
        logger.debug("Utilisateur invité créé sans base : guest");
        return new User("guest", "", authorities);
    }
}
