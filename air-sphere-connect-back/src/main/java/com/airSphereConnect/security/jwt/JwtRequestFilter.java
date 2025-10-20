package com.airSphereConnect.security.jwt;

import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.security.implementations.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        logger.debug("Filtrage JWT pour l'URL : " + request.getRequestURI());

        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    logger.debug("Token JWT trouvé dans cookie");
                    break;
                }
            }
        }

        if (jwt == null || jwt.trim().isEmpty()) {
            logger.debug("Aucun token JWT trouvé ou token vide - requête non authentifiée");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtServiceImpl.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails;
                if ("guest".equals(username)) {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
                    userDetails = new User("guest", "", authorities);
                    logger.debug("Utilisateur invité créé sans base : guest");
                } else {
                    userDetails = userDetailsService.loadUserByUsername(username);
                }

                if (jwtServiceImpl.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("Utilisateur authentifié avec succès : " + username);
                } else {
                    logger.debug("Token JWT invalide pour " + username);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la validation JWT : ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
