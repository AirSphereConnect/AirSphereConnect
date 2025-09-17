package com.airSphereConnect.exceptions;

public class GlobalException {


    public static class RessourceNotFoundException extends RuntimeException {
        public RessourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message){
            super(message);
        }
    }
}
