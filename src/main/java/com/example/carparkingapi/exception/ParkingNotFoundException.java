package com.example.carparkingapi.exception;

public class ParkingNotFoundException extends RuntimeException {
    public ParkingNotFoundException() {
    }

    public ParkingNotFoundException(String message) {
        super(message);
    }
}
