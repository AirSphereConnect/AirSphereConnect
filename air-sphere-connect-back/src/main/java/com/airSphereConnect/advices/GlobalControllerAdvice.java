package com.airSphereConnect.advices;

import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.utils.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
     * Gère l'exception ResourceNotFoundException (404 Not Found).
     * Convertit l'exception en réponse HTTP 404 avec ProblemDetail selon RFC 7807.
     *
     * @param ex l'exception ResourceNotFoundException levée
     * @return ResponseEntity avec statut 404 et détails structurés
     */
    @ExceptionHandler(GlobalException.ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(GlobalException.ResourceNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Ressource non trouvée");
        detail.setProperty("timestamp", System.currentTimeMillis());
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
    public ResponseEntity<ProblemDetail> handleBadRequestException(GlobalException.BadRequestException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        detail.setTitle("Requête invalide");
        detail.setProperty("timestamp", System.currentTimeMillis());
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
    public ResponseEntity<ProblemDetail> handleUnauthorizedException(GlobalException.UnauthorizedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        detail.setTitle("Authentification requise");
        detail.setProperty("timestamp", System.currentTimeMillis());
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
    public ResponseEntity<ProblemDetail> handleForbiddenException(GlobalException.ForbiddenException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        detail.setTitle("Accès refusé");
        detail.setProperty("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(detail);
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
