package com.example.carparkingapi.exception;

public class NoMoreElectricPlacesException extends RuntimeException {
    public NoMoreElectricPlacesException(String message) {
        super(message);
    }

    public NoMoreElectricPlacesException() {
    }
}

