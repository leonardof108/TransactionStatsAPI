package com.leonardof108.TransactionStatsAPI.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// src/main/java/com/itau/desafio/api/exception/GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle manual logic exceptions (like Future dates)
    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<Void> handleUnprocessable() {
        return ResponseEntity.unprocessableEntity().build();
    }

    // Handle JSON parsing errors (Standard Spring 400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleBadRequest() {
        return ResponseEntity.badRequest().build();
    }
}