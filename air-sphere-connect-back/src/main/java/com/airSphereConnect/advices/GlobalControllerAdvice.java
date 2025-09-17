package com.airSphereConnect.advices;

import com.airSphereConnect.exceptions.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(GlobalException.RessourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String handleRessourceNotFoundException(GlobalException.RessourceNotFoundException ex) {
        return ex.getMessage();
    }


    @ExceptionHandler(GlobalException.BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleBadRequestException(GlobalException.RessourceNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(GlobalException.UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String handleUnauthorizedException(GlobalException.UnauthorizedException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(GlobalException.ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String handleForbiddenException(GlobalException.ForbiddenException ex) {
        return ex.getMessage();
    }
}
