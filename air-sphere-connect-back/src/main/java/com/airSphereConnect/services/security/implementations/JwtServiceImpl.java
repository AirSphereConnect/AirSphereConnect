package com.airSphereConnect.services.security.implementations;

import com.airSphereConnect.services.CustomUserDetailsService;
import com.airSphereConnect.services.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implémentation du service JWT pour générer, valider et extraire les données des tokens.
 */
@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final CustomUserDetailsService userDetailsService;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    /**
     * Constructeur initialisant le service avec la clé secrète, durées des tokens, et service utilisateur.
     *
     * @param secret               clé secrète pour signature JWT
     * @param accessTokenValidity  durée d'expiration access token en ms
     * @param refreshTokenValidity durée d'expiration refresh token en ms
     * @param userDetailsService   service de chargement des informations utilisateur
     */
    public JwtServiceImpl(
            @Value("${jwt.secret:UneCleSecreteSuperLongueEtComplexePourTestUnique1234567890}") String secret,
            @Value("${jwt.access-token-validity:7200000}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity:604800000}") long refreshTokenValidity,
            CustomUserDetailsService userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Extrait le username (subject) depuis un token JWT.
     *
     * @param token token JWT
     * @return username extrait
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration d'un token JWT.
     *
     * @param token token JWT
     * @return date d'expiration
     */
    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait une information spécifique (claim) via une fonction.
     *
     * @param token          token JWT
     * @param claimsResolver fonction pour extraire la claim
     * @param <T>            type de la valeur extraite
     * @return valeur extraite de la claim
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Récupère toutes les claims dans un token JWT.
     *
     * @param token token JWT
     * @return claims extraits
     */
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si un token JWT est expiré.
     *
     * @param token token JWT
     * @return true si expiré, sinon false
     */
    @Override
    public boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    /**
     * Valide un token JWT pour un utilisateur donné.
     *
     * @param token       token JWT
     * @param userDetails données utilisateur à vérifier
     * @return true si valide, false sinon
     */
    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Génère un token JWT avec durée de validité spécifiée.
     *
     * @param userDetails données utilisateur
     * @param validity    durée en millisecondes
     * @return token JWT signé
     */
    private String generateTokenWithValidity(UserDetails userDetails, long validity) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Génère un access token JWT selon la durée configurée.
     *
     * @param userDetails données utilisateur
     * @return access token JWT
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateTokenWithValidity(userDetails, accessTokenValidity);
    }

    /**
     * Génère un token invité JWT.
     *
     * @return token invité JWT
     */
    @Override
    public String generateGuestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ROLE_GUEST"));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("guest")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Génère un refresh token avec la durée configurée.
     *
     * @param userDetails données utilisateur
     * @return refresh token JWT
     */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateTokenWithValidity(userDetails, refreshTokenValidity);
    }

    /**
     * Extrait la liste des rôles depuis un token JWT.
     *
     * @param token token JWT
     * @return liste des rôles sous forme de chaînes
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    /**
     * Charge l'objet UserDetails depuis un token JWT.
     *
     * @param token token JWT
     * @return données utilisateur chargées
     */
    @Override
    public UserDetails extractUserDetails(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    /**
     * Durée d'expiration des access tokens en secondes.
     *
     * @return durée d'expiration en secondes
     */
    @Override
    public int getAccessTokenExpirySeconds() {
        return (int) (accessTokenValidity / 1000);
    }

    /**
     * Durée d'expiration des refresh tokens en secondes.
     *
     * @return durée d'expiration en secondes
     */
    @Override
    public int getRefreshTokenExpirySeconds() {
        return (int) (refreshTokenValidity / 1000);
    }
}
