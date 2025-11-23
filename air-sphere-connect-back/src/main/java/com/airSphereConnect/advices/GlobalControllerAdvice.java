package com.airSphereConnect.advices;

import com.airSphereConnect.exceptions.GlobalException;

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

    /**
     * Gestion de l’exception ResourceNotFoundException (404).
     * Retourne un message simple avec status 404 NOT FOUND.
     *
     * @param ex exception métier ResourceNotFoundException
     * @return réponse HTTP 404 avec message d’erreur
     */
    @ExceptionHandler(GlobalException.ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(GlobalException.ResourceNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Ressource non trouvée");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    /**
     * Gestion de l’exception BadRequestException (400).
     * Retourne un message simple avec status 400 BAD REQUEST.
     *
     * @param ex exception métier BadRequestException
     * @return réponse HTTP 400 avec message d’erreur
     */
    @ExceptionHandler(GlobalException.BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequestException(GlobalException.BadRequestException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Requête invalide");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    /**
     * Gestion de l’exception UnauthorizedException (401).
     * Retourne un message simple avec status 401 UNAUTHORIZED.
     *
     * @param ex exception métier UnauthorizedException
     * @return réponse HTTP 401 avec message d’erreur
     */
    @ExceptionHandler(GlobalException.UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedException(GlobalException.UnauthorizedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        detail.setTitle("Authentification requise");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(detail);
    }

    /**
     * Gestion de l’exception ForbiddenException (403).
     * Retourne un message simple avec status 403 FORBIDDEN.
     *
     * @param ex exception métier ForbiddenException
     * @return réponse HTTP 403 avec message d’erreur
     */
    @ExceptionHandler(GlobalException.ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenException(GlobalException.ForbiddenException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setTitle("Accès refusé");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(detail);
    }

    /**
     * Gestion des exceptions non capturées autrement, retournant une réponse générique
     * avec status 500 INTERNAL SERVER ERROR.
     * Logue l’exception avec la pile complète.
     *
     * @param ex      exception inattendue levée
     * @param request requête HTTP pour récupérer l’URL
     * @return réponse HTTP 500 avec message JSON générique
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Erreur inattendue: {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue");
        detail.setTitle("Erreur interne");
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty("timestamp", Instant.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }
}
