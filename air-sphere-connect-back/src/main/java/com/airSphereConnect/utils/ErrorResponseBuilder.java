package com.airSphereConnect.utils;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Utilitaire pour construire des réponses JSON d’erreur.
 */
public final class ErrorResponseBuilder {

    private ErrorResponseBuilder() {
        // Constructeur privé pour classe utilitaire
    }

    /**
     * Construit un corps JSON d’erreur enrichi.
     *
     * @param status  le statut HTTP de la réponse
     * @param message le message d’erreur à afficher
     * @param path    l’URL de la requête générant l’erreur
     * @param errorCode un code d’erreur métier ou technique optionnel
     * @return un Map représentant la structure JSON à retourner
     */
    public static Map<String, Object> buildErrorResponse(HttpStatus status,
                                                         String message,
                                                         String path,
                                                         String errorCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        if (errorCode != null && !errorCode.isBlank()) {
            body.put("code", errorCode);
        }
        return body;
    }

    /**
     * Construit un corps JSON détaillant les erreurs de validation.
     *
     * @param fieldErrors map des champs avec leurs messages d’erreur
     * @param path        URL de la requête
     * @return structure JSON pour erreurs de validation
     */
    public static Map<String, Object> buildValidationErrorResponse(Map<String, String> fieldErrors,
                                                                   String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("message", "Certains champs sont invalides");
        body.put("fieldErrors", fieldErrors);
        body.put("path", path);
        return body;
    }
}
