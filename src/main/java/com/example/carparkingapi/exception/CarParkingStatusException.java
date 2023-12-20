package com.example.carparkingapi.exception;

public class CarParkingStatusException extends RuntimeException {
    public CarParkingStatusException() {
        super();
    }

    public CarParkingStatusException(String message) {
        super(message);
    }
}
