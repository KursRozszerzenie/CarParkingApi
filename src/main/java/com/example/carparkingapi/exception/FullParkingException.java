package com.example.carparkingapi.exception;

public class FullParkingException extends RuntimeException {
    public FullParkingException() {
    }

    public FullParkingException(String message) {
        super(message);
    }
}
