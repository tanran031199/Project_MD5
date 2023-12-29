package com.example.lamlaisecurity.config.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AppException extends RuntimeException {
    private final Integer statusCode;
    public AppException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
