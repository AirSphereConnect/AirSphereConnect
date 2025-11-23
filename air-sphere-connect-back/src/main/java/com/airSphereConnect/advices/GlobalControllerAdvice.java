package com.airSphereConnect.advices;

import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.utils.ErrorResponseBuilder;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<String> handleNotFound(GlobalException.ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Gestion de l’exception BadRequestException (400).
     * Retourne un message simple avec status 400 BAD REQUEST.
     *
     * @param ex exception métier BadRequestException
     * @return réponse HTTP 400 avec message d’erreur
     */
    @ExceptionHandler(GlobalException.BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(GlobalException.BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Gestion de l’exception UnauthorizedException (401).
     * Retourne un message simple avec status 401 UNAUTHORIZED.
     *
     * @param ex exception métier UnauthorizedException
     * @return réponse HTTP 401 avec message d’erreur
     */
    @ExceptionHandler(GlobalException.UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(GlobalException.UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    /**
     * Gestion de l’exception ForbiddenException (403).
     * Retourne un message simple avec status 403 FORBIDDEN.
     *
     * @param ex exception métier ForbiddenException
     * @return réponse HTTP 403 avec message d’erreur
     */
    @ExceptionHandler(GlobalException.ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(GlobalException.ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    /**
     * Gestion des exceptions liées à l’authentification Spring Security,
     * telles que DisabledException ou AuthenticationException.
     * Retourne un corps JSON structuré avec status 401 UNAUTHORIZED.
     *
     * @param ex      exception d’authentification
     * @param request requête HTTP pour récupérer l’URL
     * @return réponse HTTP 401 avec message JSON standardisé
     */
    @ExceptionHandler({AuthenticationException.class, DisabledException.class})
    public ResponseEntity<Map<String, Object>> handleSpringAuthExceptions(Exception ex, HttpServletRequest request) {
        logger.warn("Erreur d'authentification: {} - {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(
                ErrorResponseBuilder.buildErrorResponse(
                        HttpStatus.UNAUTHORIZED,
                        "Authentification requise ou compte désactivé",
                        request.getRequestURI(),
                        "AUTH_ERROR"
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Gestion des erreurs de validation des paramètres (@Valid) dans les requêtes HTTP.
     * Construit un corps JSON décrivant les champs en erreur avec messages associés,
     * et retourne un status 400 BAD REQUEST.
     *
     * @param ex      exception déclenchée par Spring lors d’échec de validation
     * @param request requête HTTP pour récupérer l’URL
     * @return réponse HTTP 400 avec corps JSON détaillé des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                                          HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        logger.warn("Validation failed: {} - {}", request.getRequestURI(), fieldErrors);
        return new ResponseEntity<>(
                ErrorResponseBuilder.buildValidationErrorResponse(
                        fieldErrors,
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
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
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex,
                                                                   HttpServletRequest request) {
        logger.error("Erreur inattendue: {} - {}", request.getRequestURI(), ex.getMessage(), ex);
        return new ResponseEntity<>(
                ErrorResponseBuilder.buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Une erreur interne est survenue",
                        request.getRequestURI(),
                        "INTERNAL_ERROR"
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
