package com.freelancemarketplace.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(
            ResourceAlreadyExistsException exception) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        exception.getMessage(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException exception) {

        ErrorResponse errorResponse =
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        exception.getMessage(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
}