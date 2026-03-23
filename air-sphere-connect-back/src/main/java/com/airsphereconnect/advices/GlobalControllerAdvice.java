package com.airsphereconnect.advices;

import com.airsphereconnect.exceptions.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Gestionnaire global des exceptions REST.
 * Cette classe intercepte toutes les exceptions non gérées lancées par les contrôleurs
 * et construit des réponses JSON homogènes et enrichies pour les clients.
 * Elle permet une centralisation complète de la gestion des erreurs HTTP avec log approprié.
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GlobalControllerAdvice.class);
    private static final String LOG_UNEXPECTED_ERROR = "Unexpected error on endpoint {} : {}";
    private static final String PROP_TIMESTAMP = "timestamp";


    /**
     * Gère l'exception ResourceNotFoundException (404 Not Found). Convertit l'exception en réponse HTTP 500 avec ProblemDetail selon RFC 7807.
     * @param ex l'exception ResourceNotFoundException levée
     * @return ResponseEntity avec statut 500 et détails structurés
     */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ProblemDetail> handleGlobalException(GlobalException ex, HttpServletRequest request) {
        logger.error(LOG_UNEXPECTED_ERROR, request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        detail.setTitle("Erreur applicative");
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty(PROP_TIMESTAMP, Instant.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error(LOG_UNEXPECTED_ERROR, request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue");
        detail.setTitle("Erreur interne");
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty(PROP_TIMESTAMP, Instant.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    /**
     * Gère l'exception ResourceNotFoundException (404 Not Found).
     * Convertit l'exception en réponse HTTP 404 avec ProblemDetail selon RFC 7807.
     * @param ex l'exception ResourceNotFoundException levée
     * @return ResponseEntity avec statut 404 et détails structurés
     */
    @ExceptionHandler(GlobalException.ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(GlobalException.ResourceNotFoundException ex,  HttpServletRequest request) {
        logger.error(LOG_UNEXPECTED_ERROR, request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setTitle("Ressource non trouvée");
        detail.setProperty(PROP_TIMESTAMP, System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    /**
     * Gère l'exception BadRequestException (400 Bad Request).
     * Convertit l'exception en réponse HTTP 400 avec ProblemDetail selon RFC 7807.
     *
     * @param ex l'exception BadRequestException levée
     * @return ResponseEntity avec statut 400 et détails structurés
     */
    @ExceptionHandler(GlobalException.BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequestException(GlobalException.BadRequestException ex,  HttpServletRequest request) {
        logger.error(LOG_UNEXPECTED_ERROR, request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Requête invalide");
        detail.setProperty(PROP_TIMESTAMP, System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    /**
     * Gère l'exception UnauthorizedException (401 Unauthorized).
     * Convertit l'exception en réponse HTTP 401 avec ProblemDetail selon RFC 7807.
     *
     * @param ex l'exception UnauthorizedException levée
     * @return ResponseEntity avec statut 401 et détails structurés
     */
    @ExceptionHandler(GlobalException.UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedException(GlobalException.UnauthorizedException ex,  HttpServletRequest request) {
        logger.error(LOG_UNEXPECTED_ERROR, request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setTitle("Authentification requise");
        detail.setProperty(PROP_TIMESTAMP, System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(detail);
    }

    /**
     * Gère l'exception ForbiddenException (403 Forbidden).
     * Convertit l'exception en réponse HTTP 403 avec ProblemDetail selon RFC 7807.
     *
     * @param ex l'exception ForbiddenException levée
     * @return ResponseEntity avec statut 403 et détails structurés
     */
    @ExceptionHandler(GlobalException.ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenException(GlobalException.ForbiddenException ex,  HttpServletRequest request) {
        logger.error(LOG_UNEXPECTED_ERROR, request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setTitle("Accès refusé");
        detail.setProperty(PROP_TIMESTAMP, System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(detail);
    }
}
