package com.airsphereconnect.exceptions;

/**
 * Contient les exceptions personnalisées métier utilisées dans l'application.
 * Chaque exception représente un type d'erreur métier ou de sécurité spécifique
 * avec un message explicite.
 */
public class GlobalException extends RuntimeException {

    protected GlobalException(String message) {
        super(message);
    }

    protected GlobalException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception levée lorsqu'une ressource demandée n'a pas été trouvée.
     * Correspond généralement à une erreur HTTP 404 Not Found.
     */
    public static class ResourceNotFoundException extends GlobalException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée lorsqu'une requête client est mal formée ou invalide.
     * Correspond généralement à une erreur HTTP 400 Bad Request.
     */
    public static class BadRequestException extends GlobalException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée lorsqu'un accès non autorisé est tenté.
     * Correspond généralement à une erreur HTTP 401 Unauthorized.
     */
    public static class UnauthorizedException extends GlobalException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée lorsqu'un accès interdit est détecté.
     * Correspond généralement à une erreur HTTP 403 Forbidden.
     */
    public static class ForbiddenException extends GlobalException {
        public ForbiddenException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée spécifiquement pour les erreurs liées à l'authentification JWT.
     * Utilisée notamment dans les filtres JWT lors de token invalide ou expiré.
     */
    public static class JwtAuthenticationException extends GlobalException {
        public JwtAuthenticationException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée lors d'un échec de synchronisation avec une API externe.
     */
    public static class SyncException extends GlobalException {
        public SyncException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
