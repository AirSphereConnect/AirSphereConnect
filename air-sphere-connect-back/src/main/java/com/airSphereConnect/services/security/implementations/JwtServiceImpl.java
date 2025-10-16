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

@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey secretKey;
    private final CustomUserDetailsService userDetailsService;

    //TODO il faut mette les durée dans la variable d'environnement
    // Durées en millisecondes
    private static final long ACCESS_TOKEN_VALIDITY = 2 * 60 * 60 * 1000;   // 2 heures
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 jours

    public JwtServiceImpl(@Value("${jwt.secret:UneCleSecreteSuperLongueEtComplexePourTestUnique1234567890}") String secret,
                          CustomUserDetailsService userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean isTokenExpired(String token) {
        return false;
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return "";
    }

    // Génère un access token (durée courte)
    @Override
    public String generateToken(UserDetails userDetails, long validityMillis) {
        Map<String, Object> claims = new HashMap<>();
        // Ajout des roles dans les claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateGuestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ROLE_GUEST"));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("guest")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    // Génère un refresh token (durée longue)
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, REFRESH_TOKEN_VALIDITY);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    @Override
    public UserDetails extractUserDetails(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    // Getter utilitaires pour configurer les cookies etc.
    @Override
    public int getAccessTokenExpirySeconds() {
        return (int) (ACCESS_TOKEN_VALIDITY / 1000);
    }

    @Override
    public int getRefreshTokenExpirySeconds() {
        return (int) (REFRESH_TOKEN_VALIDITY / 1000);
    }
}
