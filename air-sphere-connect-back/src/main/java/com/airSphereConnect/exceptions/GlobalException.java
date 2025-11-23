package com.airSphereConnect.exceptions;

/**
 * Contient les exceptions personnalisées métier utilisées dans l'application.
 * Chaque exception représente un type d'erreur métier ou de sécurité spécifique
 * avec un message explicite.
 */
public class GlobalException {

    /**
     * Exception levée lorsqu'une ressource demandée n'a pas été trouvée.
     * Correspond généralement à une erreur HTTP 404 Not Found.
     */
    public static class ResourceNotFoundException extends RuntimeException {
        /**
         * @param message message d'erreur détaillé
         */
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée lorsqu'une requête client est mal formée ou invalide.
     * Correspond généralement à une erreur HTTP 400 Bad Request.
     */
    public static class BadRequestException extends RuntimeException {
        /**
         * @param message message d'erreur détaillé
         */
        public BadRequestException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée lorsqu'un accès non autorisé est tenté.
     * Correspond généralement à une erreur HTTP 401 Unauthorized.
     */
    public static class UnauthorizedException extends RuntimeException {
        /**
         * @param message message d'erreur détaillé
         */
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    /**
     * Exception levée lorsqu'un accès interdit est détecté.
     * Correspond généralement à une erreur HTTP 403 Forbidden.
     */
    public static class ForbiddenException extends RuntimeException {
        /**
         * @param message message d'erreur détaillé
         */
        public ForbiddenException(String message){
            super(message);
        }
    }

    /**
     * Exception levée spécifiquement pour les erreurs liées à l'authentification JWT.
     * Utilisée notamment dans les filtres JWT lors de token invalide ou expiré.
     */
    public static class JwtAuthenticationException extends RuntimeException {
        /**
         * @param message message d'erreur détaillé
         */
        public JwtAuthenticationException(String message) {
            super(message);
        }
    }
}
