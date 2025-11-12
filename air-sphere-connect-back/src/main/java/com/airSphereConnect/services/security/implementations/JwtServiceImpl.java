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

    // Durées des tokens en millisecondes (configurables via application.yml)
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtServiceImpl(
            @Value("${jwt.secret:UneCleSecreteSuperLongueEtComplexePourTestUnique1234567890}") String secret,
            @Value("${jwt.access-token-validity:7200000}") long accessTokenValidity,  // 2h par défaut
            @Value("${jwt.refresh-token-validity:604800000}") long refreshTokenValidity, // 7j par défaut
            CustomUserDetailsService userDetailsService) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
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
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }


    // Génère un access token (durée configurable)
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Ajout des roles dans les claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidity);

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
        Date expiryDate = new Date(now.getTime() + accessTokenValidity);

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
        return generateToken(userDetails);
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
        return (int) (accessTokenValidity / 1000);
    }

    @Override
    public int getRefreshTokenExpirySeconds() {
        return (int) (refreshTokenValidity / 1000);
    }
}
