package com.example.carparkingapi.exception;

public class LPGNotAllowedException extends RuntimeException {
    public LPGNotAllowedException(String message) {
        super(message);
    }

    public LPGNotAllowedException() {
    }
}
