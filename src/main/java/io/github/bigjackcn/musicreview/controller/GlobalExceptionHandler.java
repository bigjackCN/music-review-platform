package io.github.bigjackcn.musicreview.controller;

import io.github.bigjackcn.musicreview.service.AlbumNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlbumNotFoundException.class)
    public ResponseEntity<String> handleNotFound(AlbumNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid data: please check your input (e.g. rating must be between 1 and 5)");
    }
}
