package com.example.carparkingapi.exception;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException() {
    }

    public CarNotFoundException(String message) {
        super(message);
    }
}
