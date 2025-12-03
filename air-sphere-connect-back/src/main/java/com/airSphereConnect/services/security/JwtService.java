package com.airSphereConnect.services.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * Interface définissant les opérations nécessaires à la gestion des tokens JWT.
 */
public interface JwtService {

    /**
     * Extrait le nom d'utilisateur (sujet) depuis un token JWT.
     *
     * @param token token JWT en entrée
     * @return nom d'utilisateur extrait
     */
    String extractUsername(String token);

    /**
     * Extrait la date d'expiration d'un token JWT.
     *
     * @param token token JWT
     * @return date d'expiration
     */
    Date extractExpiration(String token);

    /**
     * Extrait une information spécifique via une fonction appliquée aux claims du token.
     *
     * @param token          token JWT
     * @param claimsResolver fonction définissante comment obtenir la donnée depuis les claims
     * @param <T>            type de la donnée extraite
     * @return donnée extraite
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Extrait tous les claims contenus dans un token.
     *
     * @param token token JWT
     * @return objet Claims de la bibliothèque JWT
     */
    Claims extractAllClaims(String token);

    /**
     * Vérifie si un token est expiré.
     *
     * @param token token JWT
     * @return true si le token est expiré, false sinon
     */
    boolean isTokenExpired(String token);

    /**
     * Valide un token JWT pour un utilisateur donné.
     * Compare username et vérifie la validité temporelle.
     *
     * @param token       token JWT
     * @param userDetails données utilisateur Spring Security
     * @return true si le token est valide, false sinon
     */
    boolean validateToken(String token, UserDetails userDetails);

    /**
     * Génère un token de rafraîchissement pour un utilisateur.
     *
     * @param userDetails données utilisateur
     * @return token JWT de rafraîchissement
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Génère un token d'accès.
     *
     * @param userDetails données utilisateur
     * @return token JWT d'accès
     */
    String generateToken(UserDetails userDetails);

    /**
     * Génère un token d'accès invité.
     * Utilisé pour les connexions non authentifiées.
     *
     * @return token JWT invitée
     */
    String generateGuestToken();

    /**
     * Extrait les rôles contenus dans un token JWT.
     *
     * @param token token JWT
     * @return liste des rôles sous forme de chaînes
     */
    List<String> extractRoles(String token);

    /**
     * Extrait l'objet UserDetails complet depuis un token JWT.
     *
     * @param token token JWT
     * @return données utilisateurs chargées
     */
    UserDetails extractUserDetails(String token);

    /**
     * Durée de validité des tokens d'accès en secondes.
     *
     * @return durée d’expiration en secondes
     */
    int getAccessTokenExpirySeconds();

    /**
     * Durée de validité des tokens de rafraîchissement en secondes.
     *
     * @return durée d’expiration en secondes
     */
    int getRefreshTokenExpirySeconds();
}
