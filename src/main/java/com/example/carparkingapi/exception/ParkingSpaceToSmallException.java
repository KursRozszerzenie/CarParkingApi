package com.example.carparkingapi.exception;

public class ParkingSpaceToSmallException extends RuntimeException {
    public ParkingSpaceToSmallException() {
    }

    public ParkingSpaceToSmallException(String message) {
        super(message);
    }
}
