package com.airSphereConnect.advices;

import com.airSphereConnect.exceptions.GlobalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(GlobalException.UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(GlobalException.UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(GlobalException.CityNotFoundException.class)
    public ResponseEntity<String> handleCityNotFound(GlobalException.CityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(GlobalException.FavoriteNotFoundException.class)
    public ResponseEntity<String> handleCityNotFound(GlobalException.FavoriteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
