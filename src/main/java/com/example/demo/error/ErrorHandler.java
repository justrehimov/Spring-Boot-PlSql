package com.example.demo.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(Exception.class)
    public ErrorResponse handle(Exception ex) {
        return ErrorResponse.of(ex.getCause().getMessage());
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    static class ErrorResponse {
        private String message;
    }

}
