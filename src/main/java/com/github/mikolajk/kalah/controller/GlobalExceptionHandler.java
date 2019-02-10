package com.github.mikolajk.kalah.controller;

import com.github.mikolajk.kalah.exception.IllegalMoveException;
import com.github.mikolajk.kalah.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@EnableWebMvc
public class GlobalExceptionHandler {

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return responseEntityFromException(exception, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalMoveException.class)
    private ResponseEntity<ErrorResponse> handleIllegalMoveException(Exception exception) {
        return responseEntityFromException(exception, CONFLICT);
    }

    private ResponseEntity<ErrorResponse> responseEntityFromException(Exception exception, HttpStatus status) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage(), exception.getClass().getSimpleName()),
                status);
    }
}
