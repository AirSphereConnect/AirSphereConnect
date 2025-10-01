package com.airSphereConnect.advices;

import com.airSphereConnect.exceptions.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(GlobalException.ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(GlobalException.ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(GlobalException.BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(GlobalException.BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(GlobalException.UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(GlobalException.UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(GlobalException.ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(GlobalException.ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }


}
